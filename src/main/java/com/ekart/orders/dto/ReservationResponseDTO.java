package com.ekart.orders.dto;

public class ReservationResponseDTO {

    private Long orderId;
    private String status;
    private Long productId;
    private Long quantity;
    private Long remainingQty;

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(Long remainingQty) {
        this.remainingQty = remainingQty;
    }
}
