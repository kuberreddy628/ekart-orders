package com.ekart.orders.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
public class Orders {

    @Id
    @GeneratedValue(strategy= GenerationType. IDENTITY)
    private Long orderId;
    private Long customerId;

    @Enumerated (EnumType.STRING)
    private OrderStatus status;
    private BigDecimal total_Amount;

    private LocalDateTime created_At;
    private LocalDateTime updated_At;

    /** Persisted delivery address so shipping can be requested asynchronously via Kafka without replaying OrderPlacedEvent. */
    private String shipHouseNumber;
    private String shipArea;
    private String shipCity;
    private String shipState;
    private String shipPincode;

    /** Set when payment completes asynchronously ({@code payment-success} Kafka topic). */
    private Long paymentId;

    /** Set when shipping completes asynchronously ({@code shipping-success} Kafka topic). */
    private Long shipmentId;

    private String shipmentStatusSummary;

    private String trackingNumber;

    /** Non-null after {@code shipping-failure} Kafka outcome. */
    private String shippingFailureReason;

    @OneToMany(mappedBy ="orders", cascade =CascadeType.ALL, orphanRemoval=true)
    private List<OrderItems> orderItems = new ArrayList<>();

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotal_Amount() {
        return total_Amount;
    }

    public void setTotal_Amount(BigDecimal total_Amount) {
        this.total_Amount = total_Amount;
    }

    public LocalDateTime getCreated_At() {
        return created_At;
    }

    public void setCreated_At(LocalDateTime created_At) {
        this.created_At = created_At;
    }

    public LocalDateTime getUpdated_At() {
        return updated_At;
    }

    public void setUpdated_At(LocalDateTime updated_At) {
        this.updated_At = updated_At;
    }

    public List<OrderItems> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItems> orderItems) {
        this.orderItems = orderItems;
    }

    public String getShipHouseNumber() {
        return shipHouseNumber;
    }

    public void setShipHouseNumber(String shipHouseNumber) {
        this.shipHouseNumber = shipHouseNumber;
    }

    public String getShipArea() {
        return shipArea;
    }

    public void setShipArea(String shipArea) {
        this.shipArea = shipArea;
    }

    public String getShipCity() {
        return shipCity;
    }

    public void setShipCity(String shipCity) {
        this.shipCity = shipCity;
    }

    public String getShipState() {
        return shipState;
    }

    public void setShipState(String shipState) {
        this.shipState = shipState;
    }

    public String getShipPincode() {
        return shipPincode;
    }

    public void setShipPincode(String shipPincode) {
        this.shipPincode = shipPincode;
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

    public String getShipmentStatusSummary() {
        return shipmentStatusSummary;
    }

    public void setShipmentStatusSummary(String shipmentStatusSummary) {
        this.shipmentStatusSummary = shipmentStatusSummary;
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
