package com.ekart.orders.service;

import com.ekart.orders.dto.*;
import com.ekart.orders.entity.OrderStatus;
import com.ekart.orders.event.fulfillment.InventoryOrderOutcomeEvent;
import com.ekart.orders.event.fulfillment.PaymentOutcomeEvent;
import com.ekart.orders.event.fulfillment.ShippingOutcomeEvent;

import java.util.List;

public interface OrderService {

    OrderConfirmationResponseDTO placeOrder(OrdersRequestDTO OrdersRequestDTO);

    /** Kafka: {@code inventory-order-success} */
    void handleInventoryOrderSuccess(InventoryOrderOutcomeEvent eventPayload);

    /** Kafka: {@code inventory-order-failure} */
    void handleInventoryOrderFailure(InventoryOrderOutcomeEvent eventPayload);

    /** Kafka: {@code payment-success} */
    void handlePaymentSuccess(PaymentOutcomeEvent eventPayload);

    /** Kafka: {@code payment-failure} */
    void handlePaymentFailure(PaymentOutcomeEvent eventPayload);

    /** Kafka: {@code shipping-success} */
    void handleShippingSuccess(ShippingOutcomeEvent eventPayload);

    /** Kafka: {@code shipping-failure} */
    void handleShippingFailure(ShippingOutcomeEvent eventPayload);
    OrderDetailsResponseDTO getOrderDetailsByID(Long id);
    List<OrderItemDetailsResponseDTO> getAllOrdersOfCustomer (Long gusomerID);
    List<OrderDetailsResponseDTO> getAllOrders();
    StatusUpdateResponseDTO updateOrderStatus(Long id, OrderStatus status);
    //OrdersResponseDTO CancelOrder (Long orderID);
    CancelOrderResponseDTO CancelOrder (Long orderID);
}
