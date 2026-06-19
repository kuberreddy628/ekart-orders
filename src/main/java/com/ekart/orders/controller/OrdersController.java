package com.ekart.orders.controller;

import com.ekart.orders.dto.*;
import com.ekart.orders.entity.OrderStatus;
import com.ekart.orders.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController

@RequestMapping("/orders")
public class OrdersController {

    private static final Logger logger = LoggerFactory.getLogger(OrdersController.class);

    private OrderService orderService;

    @Autowired
    public OrdersController( OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrdersRequestDTO requestDTO, @NotNull BindingResult result) {
        if (result.hasErrors()) {

            String message = result.getAllErrors().stream().map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(","));
            logger.warn("validations failed message: {}", message);
            return ResponseEntity.badRequest().body(message);
        }
        logger.info("Received place order request for userID:{}", requestDTO.getCustomerID());

        try {
            OrderConfirmationResponseDTO responseDTO = orderService.placeOrder(requestDTO);
            logger.info("order successfully placed. orderID:{}", responseDTO.getOrderId());
            return ResponseEntity.ok(responseDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/ordersById/{orderID}")
    public ResponseEntity<OrderDetailsResponseDTO> getOrderDetailsByID(@PathVariable("orderID") Long orderID) {
        logger.info("Received request to get order details by ID:{}", orderID);
        return ResponseEntity.ok(orderService.getOrderDetailsByID(orderID));
    }

    @GetMapping("/ordersByCustomer/{customerID}")
    public ResponseEntity<List<OrderItemDetailsResponseDTO>> getAllOrdersOfCustomer(@PathVariable("customerID") Long customerID) {
        logger.info("Received request to get orders of customer with ID:{}", customerID);
        return ResponseEntity.ok(orderService.getAllOrdersOfCustomer(customerID));
    }

        @GetMapping("/getAllOrders")
        public ResponseEntity<List<OrderDetailsResponseDTO>> getAllOrders(){
            logger.info("Received request to get all the orders");
            return ResponseEntity.ok(orderService.getAllOrders());
        }

        @PatchMapping("/updateStatus/{orderID}")
        public ResponseEntity<?> updateOrderStatus (@PathVariable("orderID") Long orderID, @Valid @RequestBody StatusUpdateRequestDTO status, BindingResult result) {
            try {
                if (!result.hasErrors()) {
                    logger.info("Received request to update the status of order:{}", orderID);
                    StatusUpdateResponseDTO dto = orderService.updateOrderStatus(orderID, OrderStatus.valueOf(status.getStatus()));
                    logger.info("Received request to update the status of order:{}", orderID);
                    logger.info("status is updated successfully Status:{}", dto.getStatus());
                    return ResponseEntity.ok(dto);
                }
                String errorlist = result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
                return ResponseEntity.badRequest().body(errorlist);
            } catch (EntityNotFoundException e) {
                logger.error("order not found with order10:{}", orderID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found with ID: "+ orderID);
            }
        }

                @DeleteMapping("/deleteByOrderID/{orderID}")
                public ResponseEntity<?> cancelOrder(@PathVariable("orderID") Long orderID) {
                    logger.info("Received request to cancel order by ID:{}", orderID);
                    try{
                    CancelOrderResponseDTO responseDTO = orderService.CancelOrder(orderID);
                    if ((responseDTO != null)) {
                        logger.info("Order Cancelled successfully with orderID:{}",orderID);
                    }
                    return ResponseEntity.ok(responseDTO);
                } catch (EntityNotFoundException e) {
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }
        }
