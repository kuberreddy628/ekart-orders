package com.ekart.orders.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelProductsDTO {

    private Long productId;
    private Long releasedQty;
    private Long availableQty;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getReleasedQty() {
        return releasedQty;
    }

    public void setReleasedQty(Long releasedQty) {
        this.releasedQty = releasedQty;
    }

    public Long getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Long availableQty) {
        this.availableQty = availableQty;
    }
}
