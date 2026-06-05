package com.ekart.orders.listener;

import com.ekart.orders.event.fulfillment.InventoryOrderOutcomeEvent;
import com.ekart.orders.event.fulfillment.PaymentOutcomeEvent;
import com.ekart.orders.event.fulfillment.ShippingOutcomeEvent;
import com.ekart.orders.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FulfillmentKafkaListener {

    private final OrderService orderService;

    public FulfillmentKafkaListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
            topics = "inventory-order-success",
            groupId = "orders-fulfillment-inventory",
            containerFactory = "inventoryOutcomeKafkaListenerContainerFactory")
    public void onInventoryOrderSuccess(InventoryOrderOutcomeEvent event) {
        orderService.handleInventoryOrderSuccess(event);
    }

    @KafkaListener(
            topics = "inventory-order-failure",
            groupId = "orders-fulfillment-inventory",
            containerFactory = "inventoryOutcomeKafkaListenerContainerFactory")
    public void onInventoryOrderFailure(InventoryOrderOutcomeEvent event) {
        orderService.handleInventoryOrderFailure(event);
    }

    @KafkaListener(
            topics = "payment-success",
            groupId = "orders-fulfillment-payment",
            containerFactory = "paymentOutcomeKafkaListenerContainerFactory")
    public void onPaymentSuccess(PaymentOutcomeEvent event) {
        orderService.handlePaymentSuccess(event);
    }

    @KafkaListener(
            topics = "payment-failure",
            groupId = "orders-fulfillment-payment",
            containerFactory = "paymentOutcomeKafkaListenerContainerFactory")
    public void onPaymentFailure(PaymentOutcomeEvent event) {
        orderService.handlePaymentFailure(event);
    }

    @KafkaListener(
            topics = "shipping-success",
            groupId = "orders-fulfillment-shipping",
            containerFactory = "shippingOutcomeKafkaListenerContainerFactory")
    public void onShippingSuccess(ShippingOutcomeEvent event) {
        orderService.handleShippingSuccess(event);
    }

    @KafkaListener(
            topics = "shipping-failure",
            groupId = "orders-fulfillment-shipping",
            containerFactory = "shippingOutcomeKafkaListenerContainerFactory")
    public void onShippingFailure(ShippingOutcomeEvent event) {
        orderService.handleShippingFailure(event);
    }
}
