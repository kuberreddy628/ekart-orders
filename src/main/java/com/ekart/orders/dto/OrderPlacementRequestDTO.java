package com.ekart.orders.dto;

public class OrderPlacementRequestDTO {

    private Long orderId;
    private Long productId;
    private Long reservedQty;              //requstedQnty suits best here

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getReservedQty() {
        return reservedQty;
    }

    public void setReservedQty(Long reservedQty) {
        this.reservedQty = reservedQty;
    }
}
