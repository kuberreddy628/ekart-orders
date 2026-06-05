package com.ekart.orders.service.model;

import com.ekart.orders.entity.OrderItems;

import java.math.BigDecimal;
import java.util.List;

public class OrderBuildResult {
    private List<OrderItems> orderItems;
    private BigDecimal totalAmount;

    public OrderBuildResult(List<OrderItems> orderItems, BigDecimal totalAmount) {
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
    }

    public List<OrderItems> getOrderItems() {
        return orderItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

}
