package com.ekart.orders.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class OrderItemsDTO {

    @NotNull(message = "product ID is required")
    private Long productID;

    @Min(value = 1, message= "quantity must be minimum 1")
    private Long quantity;

   // @DecimalMin(value = "0.01", message ="price must be positive value")
   // private BigDecimal price;

    public Long getProductID() {
        return productID;
    }

    public void setProductID(Long productID) {
        this.productID = productID;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    /*public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }*/
}
