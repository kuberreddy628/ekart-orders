package com.ekart.orders.dto;
import java.math.BigDecimal;

public class ProductDetailsDTO {

    private Long productID;
    private String name;
    private String description;
    private BigDecimal price;
    private Long requestedQty;
    private Long availableQty;

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
