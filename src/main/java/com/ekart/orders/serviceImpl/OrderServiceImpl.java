package com.ekart.orders.serviceImpl;

import com.ekart.orders.Mapper.OrderMapper;
import com.ekart.orders.client.InventoryClient;
import com.ekart.orders.dto.*;
import com.ekart.orders.entity.OrderItems;
import com.ekart.orders.entity.OrderStatus;
import com.ekart.orders.entity.Orders;
import com.ekart.orders.event.OrderPlacedEvent;
import com.ekart.orders.event.fulfillment.InventoryOrderOutcomeEvent;
import com.ekart.orders.event.fulfillment.PaymentRequestEvent;
import com.ekart.orders.event.fulfillment.PaymentOutcomeEvent;
import com.ekart.orders.event.fulfillment.ShippingAddressPayload;
import com.ekart.orders.event.fulfillment.ShippingRequestEvent;
import com.ekart.orders.event.fulfillment.ShippingOutcomeEvent;
import com.ekart.orders.repository.OrdersRepository;
import com.ekart.orders.service.OrderService;
import com.ekart.orders.service.model.OrderBuildResult;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String TOPIC_PAYMENT_REQUEST = "payment-request";
    private static final String TOPIC_SHIPPING_REQUEST = "shipping-request";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrdersRepository repository;

    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    public OrderServiceImpl(OrdersRepository repository){
        this.repository = repository;
    }

    @Transactional
    @Override
    public OrderConfirmationResponseDTO placeOrder(OrdersRequestDTO ordersRequestDTO) {

        logger.info("Received request to place an order for userID:{}", ordersRequestDTO.getCustomerID());
        Orders orders = new Orders();
        AddressDTO addressDTO = ordersRequestDTO.getAddress();

        OrderBuildResult result = buildOrderItemsAndTotal( ordersRequestDTO,  orders );
        List<OrderItems> orderItems = result.getOrderItems();
        BigDecimal totalAmount =  result.getTotalAmount();

        Orders savedOrders = saveOrderAndPublish( ordersRequestDTO, orders,  orderItems, totalAmount, addressDTO);
        OrderConfirmationResponseDTO confirmationResponseDTO = new OrderConfirmationResponseDTO();
        confirmationResponseDTO.setOrderId(savedOrders.getOrderId());
        confirmationResponseDTO.setStatus(OrderStatus.ORDER_PLACED.name());
        return confirmationResponseDTO;
    }
    // we are getting data using findById(). since we have proper sappings implemented otherwise we should

    //go for HQL to fetch data from 2 tables


    private OrderBuildResult buildOrderItemsAndTotal(OrdersRequestDTO request, Orders orders) {

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItems> orderItems =  new ArrayList<>();

        for (OrderItemsDTO dto : request.getItems()) {
            ProductDetailsDTO product = inventoryClient.getProductByID(dto.getProductID());

            if (product == null) {
                throw new EntityNotFoundException("No Product found with Id:" + dto.getProductID());
            }

            OrderItems item = new OrderItems();
            item.setProductId(dto.getProductID());
            item.setQuantity(dto.getQuantity());
            item.setPrice( product.getPrice());

            BigDecimal itemPrice = product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity() ) );

            item.setTotalPrice(itemPrice);
            totalAmount = totalAmount.add(itemPrice);

            item.setOrders(orders);
            orderItems.add(item);
        }
        return new OrderBuildResult(orderItems, totalAmount);
    }

    private Orders saveOrderAndPublish( OrdersRequestDTO request, Orders orders, List<OrderItems> orderItems, BigDecimal totalAmount, AddressDTO addressDTO) {

        orders.setCustomerId( request.getCustomerID() );

        orders.setStatus(OrderStatus.ORDER_PLACED);

        orders.setTotal_Amount( totalAmount );

        orders.setOrderItems( orderItems  );

        orders.setCreated_At( LocalDateTime.now() );

        orders.setUpdated_At(  LocalDateTime.now());

        if (addressDTO != null) {
            orders.setShipHouseNumber(addressDTO.getHouseNumber());
            orders.setShipArea(addressDTO.getArea());
            orders.setShipCity(addressDTO.getCity());
            orders.setShipState(addressDTO.getState());
            orders.setShipPincode(addressDTO.getPincode());
        }

        Orders savedOrders = repository.save(orders);

        OrderPlacedEvent event = OrderMapper.toEvent(savedOrders, addressDTO);

        /*
         * Publish only after DB commit. Sending inside @Transactional placeOrder() lets consumers
         * see inventory-order-success before this transaction commits; handleInventoryOrderSuccess then
         * runs findById on another connection and fails with EntityNotFoundException (row not visible yet),
         * so payment/shipping never start.
         */
        final Long publishedOrderId = savedOrders.getOrderId();
        final OrderPlacedEvent eventToPublish = event;

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    kafkaTemplate.send("order-placed", String.valueOf(publishedOrderId), eventToPublish);
                    logger.info("Published order-placed after DB commit, orderId={}", publishedOrderId);
                }
            });
        } else {
            kafkaTemplate.send("order-placed", String.valueOf(publishedOrderId), eventToPublish);
            logger.warn(
                    "No Spring transaction active while placing order; sent order-placed immediately. orderId={}",
                    publishedOrderId);
        }
        return savedOrders;
    }

    @Transactional
    @Override
    public void handleInventoryOrderSuccess(InventoryOrderOutcomeEvent eventPayload) {
        if (eventPayload == null || eventPayload.getOrderId() == null) {
            logger.warn("Ignored inventory-order-success without orderId");
            return;
        }
        Long orderId = eventPayload.getOrderId();
        Orders orders = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID:" + orderId));
        if (orders.getStatus() != OrderStatus.ORDER_PLACED) {
            logger.info("Skip inventory-order-success for orderId={}, currentStatus={}", orderId, orders.getStatus());
            return;
        }
        orders.setStatus(OrderStatus.CONFIRMED);
        orders.setUpdated_At(LocalDateTime.now());
        repository.save(orders);

        PaymentRequestEvent paymentRequest = new PaymentRequestEvent();
        paymentRequest.setOrderId(orderId);
        paymentRequest.setAmount(orders.getTotal_Amount());
        paymentRequest.setCurrency("INR");
        paymentRequest.setIdempotencyKey("ORDER-" + orderId);
        final PaymentRequestEvent paymentToPublish = paymentRequest;
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    kafkaTemplate.send(TOPIC_PAYMENT_REQUEST, String.valueOf(orderId), paymentToPublish);
                    logger.info("Published {} for orderId={}", TOPIC_PAYMENT_REQUEST, orderId);
                }
            });
        } else {
            kafkaTemplate.send(TOPIC_PAYMENT_REQUEST, String.valueOf(orderId), paymentToPublish);
            logger.info("Published {} for orderId={}", TOPIC_PAYMENT_REQUEST, orderId);
        }
    }

    @Transactional
    @Override
    public void handleInventoryOrderFailure(InventoryOrderOutcomeEvent eventPayload) {
        if (eventPayload == null || eventPayload.getOrderId() == null) {
            logger.warn("Ignored inventory-order-failure without orderId");
            return;
        }
        Long orderId = eventPayload.getOrderId();
        Orders orders = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID:" + orderId));
        if (orders.getStatus() != OrderStatus.ORDER_PLACED) {
            logger.info("Skip inventory-order-failure for orderId={}, currentStatus={}", orderId, orders.getStatus());
            return;
        }
        orders.setStatus(OrderStatus.CANCELLED);
        orders.setUpdated_At(LocalDateTime.now());
        repository.save(orders);
        logger.warn("Order cancelled after inventory failure. orderId={}, reason={}", orderId, eventPayload.getFailureReason());
    }

    @Transactional
    @Override
    public void handlePaymentSuccess(PaymentOutcomeEvent eventPayload) {
        if (eventPayload == null || eventPayload.getOrderId() == null) {
            logger.warn("Ignored payment-success without orderId");
            return;
        }
        Long orderId = eventPayload.getOrderId();
        Orders orders = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID:" + orderId));
        if (orders.getStatus() != OrderStatus.CONFIRMED) {
            logger.info("Skip payment-success for orderId={}, currentStatus={}", orderId, orders.getStatus());
            return;
        }
        if (eventPayload.getStatus() != null && !"SUCCESS".equalsIgnoreCase(eventPayload.getStatus())) {
            logger.warn("payment-success topic received non-success status {} for orderId={}", eventPayload.getStatus(), orderId);
            return;
        }

        orders.setStatus(OrderStatus.PAYMENT_SUCCESS);
        orders.setPaymentId(eventPayload.getPaymentId());
        orders.setUpdated_At(LocalDateTime.now());
        repository.save(orders);

        ShippingRequestEvent shippingRequest = new ShippingRequestEvent();
        shippingRequest.setOrderId(orderId);
        ShippingAddressPayload addressPayload = new ShippingAddressPayload();
        addressPayload.setHouseNumber(orders.getShipHouseNumber());
        addressPayload.setArea(orders.getShipArea());
        addressPayload.setCity(orders.getShipCity());
        addressPayload.setState(orders.getShipState());
        addressPayload.setPincode(orders.getShipPincode());
        shippingRequest.setAddress(addressPayload);

        final ShippingRequestEvent shippingToPublish = shippingRequest;
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    kafkaTemplate.send(TOPIC_SHIPPING_REQUEST, String.valueOf(orderId), shippingToPublish);
                    logger.info("Published {} for orderId={}", TOPIC_SHIPPING_REQUEST, orderId);
                }
            });
        } else {
            kafkaTemplate.send(TOPIC_SHIPPING_REQUEST, String.valueOf(orderId), shippingToPublish);
            logger.info("Published {} for orderId={}", TOPIC_SHIPPING_REQUEST, orderId);
        }
    }

    @Transactional
    @Override
    public void handlePaymentFailure(PaymentOutcomeEvent eventPayload) {
        if (eventPayload == null || eventPayload.getOrderId() == null) {
            logger.warn("Ignored payment-failure without orderId");
            return;
        }
        Long orderId = eventPayload.getOrderId();
        Orders orders = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID:" + orderId));
        if (orders.getStatus() != OrderStatus.CONFIRMED) {
            logger.info("Skip payment-failure for orderId={}, currentStatus={}", orderId, orders.getStatus());
            return;
        }
        orders.setStatus(OrderStatus.PAYMENT_FAILED);
        orders.setUpdated_At(LocalDateTime.now());
        repository.save(orders);
        try {
            inventoryClient.cancelOrder(orderId);
        } catch (Exception ex) {
            logger.warn("Could not cancel inventory reservations for orderId={}: {}", orderId, ex.getMessage());
        }
        logger.warn("Payment failed for orderId={}, message={}", orderId, eventPayload.getMessage());
    }

    @Transactional
    @Override
    public void handleShippingSuccess(ShippingOutcomeEvent eventPayload) {
        if (eventPayload == null || eventPayload.getOrderId() == null) {
            logger.warn("Ignored shipping-success without orderId");
            return;
        }
        Long orderId = eventPayload.getOrderId();
        Orders orders = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID:" + orderId));
        if (orders.getStatus() != OrderStatus.PAYMENT_SUCCESS) {
            logger.info("Skip shipping-success for orderId={}, currentStatus={}", orderId, orders.getStatus());
            return;
        }
        orders.setStatus(OrderStatus.SHIPPED);
        orders.setShipmentId(eventPayload.getShipmentId());
        orders.setTrackingNumber(eventPayload.getTrackingNumber());
        orders.setShipmentStatusSummary(eventPayload.getShipmentStatus());
        orders.setShippingFailureReason(null);
        orders.setUpdated_At(LocalDateTime.now());
        repository.save(orders);
        logger.info("Order shipped. orderId={}, tracking={}", orderId, eventPayload.getTrackingNumber());
    }

    @Transactional
    @Override
    public void handleShippingFailure(ShippingOutcomeEvent eventPayload) {
        if (eventPayload == null || eventPayload.getOrderId() == null) {
            logger.warn("Ignored shipping-failure without orderId");
            return;
        }
        Long orderId = eventPayload.getOrderId();
        Orders orders = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID:" + orderId));
        if (orders.getStatus() != OrderStatus.PAYMENT_SUCCESS) {
            logger.info("Skip shipping-failure for orderId={}, currentStatus={}", orderId, orders.getStatus());
            return;
        }
        orders.setStatus(OrderStatus.SHIPPING_FAILED);
        orders.setShippingFailureReason(eventPayload.getFailureReason());
        orders.setUpdated_At(LocalDateTime.now());
        repository.save(orders);
        logger.warn("Shipping failed for orderId={}, reason={}", orderId, eventPayload.getFailureReason());
    }

            @Transactional
            @Override
            public OrderDetailsResponseDTO getOrderDetailsByID(Long orderID) {
                logger.info("Received request to get orders by orderID:{}", orderID);
                Orders orders = repository.findById(orderID).orElseThrow();
                logger.info("Received order details from db:{}", orders.getOrderId());
                List<OrderItems> orderItems = orders.getOrderItems();
                List<OrderItemDetailsResponseDTO> orderedItems = new ArrayList<>();

                OrderDetailsResponseDTO responseDTO = new OrderDetailsResponseDTO();
                responseDTO.setCustomerID(orders.getCustomerId());
                responseDTO.setOrderID(orders.getOrderId());
                responseDTO.setStatus(orders.getStatus());
                responseDTO.setTotalAmount(orders.getTotal_Amount());
                responseDTO.setPaymentId(orders.getPaymentId());
                responseDTO.setShipmentId(orders.getShipmentId());
                responseDTO.setShipmentStatus(orders.getShipmentStatusSummary());
                responseDTO.setTrackingNumber(orders.getTrackingNumber());
                responseDTO.setShippingFailureReason(orders.getShippingFailureReason());

                for (OrderItems items : orderItems) {
                    OrderItemDetailsResponseDTO itemDto = new OrderItemDetailsResponseDTO();
                    itemDto.setQuantity(items.getQuantity());
                    itemDto.setPrice(items.getPrice());
                    itemDto.setProductId(items.getProductId());
                    itemDto.setTotalPrice(items.getTotalPrice());
                    orderedItems.add(itemDto);
                }
                responseDTO.setOrderItems(orderedItems);
                return responseDTO;
            }
                @Override
                public List<OrderItemDetailsResponseDTO> getAllOrdersOfCustomer(Long customerID) {
                    logger.info("Received request to get all orders by customerID:{}", customerID);
                    Orders orders = repository.findByCustomerId(customerID).orElseThrow(()->new EntityNotFoundException("No Orders found for the given customerID :" + customerID));
                   /* if(orders == null){
                        throw new EntityNotFoundException("No Orders found for the given customerID"+customerID);
                    }*/
                    logger.info("Retrieved partial order details with customerID:{}", orders.getCustomerId());
                    logger.info("Getting all the details of customer by orderID:{}", orders.getOrderId());
                    Orders orders1 = repository.findById(orders.getOrderId()).orElseThrow(()->new EntityNotFoundException("No Orders found for the given customerID:{}"+customerID));
                    logger.info("Retrieved all the product details of customer with orderID:{}", orders1.getOrderId());

                    List<OrderItemDetailsResponseDTO> itemslist = new ArrayList();
                    List<OrderItems> orderItems = orders1.getOrderItems();
                    for (OrderItems items : orderItems) {
                        OrderItemDetailsResponseDTO responseDTO = new OrderItemDetailsResponseDTO();
                        responseDTO.setProductId(items.getProductId());
                        responseDTO.setPrice(items.getPrice());
                        responseDTO.setQuantity(items.getQuantity());
                        responseDTO.setTotalPrice(items.getTotalPrice());

                        itemslist.add(responseDTO);
                    }
                    return itemslist;
                }
                    @Override
                    public List<OrderDetailsResponseDTO> getAllOrders() {
                        logger.info("Received request to get all the orders");
                        List<Orders> orders = repository.findAll();
                        logger.info("Received all the orders from db");
                        List<OrderDetailsResponseDTO> orderDetailsResponseDTOList = new ArrayList<>();

                        for(Orders orders1: orders) {
                            OrderDetailsResponseDTO detailsResponseDTO = new OrderDetailsResponseDTO();
                            detailsResponseDTO.setOrderID(orders1.getOrderId());
                            detailsResponseDTO.setCustomerID(orders1.getCustomerId());
                            detailsResponseDTO.setStatus(orders1.getStatus());
                            detailsResponseDTO.setTotalAmount(orders1.getTotal_Amount());
                            List<OrderItemDetailsResponseDTO> list = new ArrayList();
                            List<OrderItems> orderItems = orders1.getOrderItems();
                            for (OrderItems items : orderItems) {
                                OrderItemDetailsResponseDTO itemDetailsResponseDTO = new OrderItemDetailsResponseDTO();
                                itemDetailsResponseDTO.setProductId(items.getProductId());
                                itemDetailsResponseDTO.setQuantity(items.getQuantity());
                                itemDetailsResponseDTO.setPrice(items.getPrice());
                                itemDetailsResponseDTO.setTotalPrice(items.getTotalPrice());
                                list.add(itemDetailsResponseDTO);
                            }
                            detailsResponseDTO.setOrderItems(list);
                            orderDetailsResponseDTOList.add(detailsResponseDTO);
                        }
                        return orderDetailsResponseDTOList;
                    }

                    @Override
                    public StatusUpdateResponseDTO updateOrderStatus (Long id, OrderStatus status) {
                        logger.info("Received request to update the status.orderID:{}", + id);
                        Orders orders = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("order not found with ID:" + id));
                        orders.setStatus(status);
                        repository.save(orders);
                        logger.info("updated the status of orderID: {}, status: {}", id, orders.getStatus());
                        StatusUpdateResponseDTO updateResponseDTO = new StatusUpdateResponseDTO();
                        updateResponseDTO.setOrderID(orders.getOrderId());
                        updateResponseDTO.setStatus(orders.getStatus());
                        updateResponseDTO.setTotalAmount(orders.getTotal_Amount());
                        return updateResponseDTO;
                    }
                        @Transactional
                        @Override
                        public CancelOrderResponseDTO CancelOrder(Long orderID) {
                            logger.info("Received request to cancel the order ID:{}", orderID);
                            logger.info("Retrieving order details with order ID:{}", orderID);
                            Orders orders = repository.findById(orderID).orElseThrow(()->new EntityNotFoundException("Do not find Order with orderID: "+orderID));
                            logger.info("calling delete method to delete the order");
                            repository.deleteById(orderID);
                            logger.info("Order Cancellation success in orders");
                            CancelOrderResponseDTO responseDTO = inventoryClient.cancelOrder(orderID);
                        //OrdersResponseDTO ordersResponseDTO = responseDTO, getOrdersResponseDTO();
                            CancelOrderResponseDTO cancelOrderResponseDTO = new CancelOrderResponseDTO();
                            List<CancelProductsDTO> list = new ArrayList<>();

                           /* OrdersResponseDTO dto = new OrdersResponseDTO();
                           dto.setOrderID(ordersResponseDTO.getOrderID());
                            dto.setStatus(ordersResponseDTO.getStatus());
                            dto.setTotalAmount(orders.getTotal_Amount());
                            cancelOrderResponseDTO.setOrdersResponseDTO (dto);*/

                            cancelOrderResponseDTO.setOrderId(responseDTO.getOrderId());
                            cancelOrderResponseDTO.setStatus(responseDTO.getStatus());
                            for (CancelProductsDTO list0fCancelProducts: responseDTO.getReleasedItems()) {
                                CancelProductsDTO cancelProductsDTO = new CancelProductsDTO();
                                cancelProductsDTO.setProductId(list0fCancelProducts.getProductId());
                                cancelProductsDTO.setAvailableQty(list0fCancelProducts.getAvailableQty());
                                cancelProductsDTO.setReleasedQty(list0fCancelProducts.getReleasedQty());
                                list.add(cancelProductsDTO);
                            }
                            cancelOrderResponseDTO.setReleasedItems(list);
                            OrdersResponseDTO dto = new OrdersResponseDTO();
                            dto.setOrderID(orders.getOrderId());
                            dto.setStatus(OrderStatus.CANCELLED);
                            dto.setTotalAmount(orders.getTotal_Amount());
                            logger.info("Sending response back to controller with cancelled order details");
                            return cancelOrderResponseDTO;

                        }
                    }

