package com.ekart.orders.dto;

/**
 * Minimal response for {@code POST} place-order: async fulfillment details come from
 * {@code GET /orders/ordersById/{orderId}}.
 */
public class OrderConfirmationResponseDTO {

    private Long orderId;
    private String status;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
