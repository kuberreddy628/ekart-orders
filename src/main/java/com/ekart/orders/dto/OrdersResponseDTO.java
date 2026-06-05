package com.ekart.orders.dto;

import com.ekart.orders.entity.OrderStatus;

import java.math.BigDecimal;

public class OrdersResponseDTO {

    private Long orderID;
    private OrderStatus status;
    private BigDecimal totalAmount;

    public OrdersResponseDTO() {
    }

    public OrdersResponseDTO(Long orderID, OrderStatus status, BigDecimal totalAmount) {
        this.orderID = orderID;
        this.status = status;
        this.totalAmount = totalAmount;
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
}
