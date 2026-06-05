package com.ekart.orders.dto;

import com.ekart.orders.entity.OrderStatus;

import java.math.BigDecimal;

public class StatusUpdateResponseDTO {

    private Long OrderID;
    private OrderStatus status;
    private BigDecimal totalAmount;

    public Long getOrderID() {
        return OrderID;
    }

    public void setOrderID(Long orderID) {
        OrderID = orderID;
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
