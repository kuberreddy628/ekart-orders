package com.ekart.orders.dto;

import com.ekart.orders.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public class OrderDetailsResponseDTO {

    private Long customerID;
    private Long orderID;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemDetailsResponseDTO> orderItems;

    /** Populated after asynchronous payment completes (Kafka). */
    private Long paymentId;

    /** Populated after asynchronous shipping completes (Kafka). */
    private Long shipmentId;

    private String shipmentStatus;

    private String trackingNumber;

    private String shippingFailureReason;

    public Long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemDetailsResponseDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDetailsResponseDTO> orderItems) {
        this.orderItems = orderItems;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Long shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getShippingFailureReason() {
        return shippingFailureReason;
    }

    public void setShippingFailureReason(String shippingFailureReason) {
        this.shippingFailureReason = shippingFailureReason;
    }
}
