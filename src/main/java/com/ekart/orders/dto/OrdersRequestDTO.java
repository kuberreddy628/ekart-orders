package com.ekart.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrdersRequestDTO {

    @NotNull(message = "customerID is required")
    private Long customerID;

    @Valid
    @NotEmpty(message = "order items cannot be empty")
    private List<OrderItemsDTO> items;

    @Valid
    private AddressDTO address;

    public Long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public List<OrderItemsDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemsDTO> items) {
        this.items = items;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

}
