package com.ekart.orders.dto;

import jakarta.validation.constraints.NotEmpty;

public class StatusUpdateRequestDTO {

    @NotEmpty(message= "status is required")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
