package com.ekart.orders.event;

import java.math.BigDecimal;

public class ProductDetailsResponseEvent {

    private Long orderID;
    private Long productID;
    private String name;
    private String description;
    private BigDecimal price;
    private Long requestedQty;
    private Long availableQty;

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(Long requestedQty) {
        this.requestedQty = requestedQty;
    }

    public Long getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Long availableQty) {
        this.availableQty = availableQty;
    }
}
