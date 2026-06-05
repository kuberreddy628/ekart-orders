package com.ekart.orders.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OrderPlacedEvent {

    /** Serialized as {@code orderId} so downstream services (inventory) deserialize reliably. */
    @JsonProperty("orderId")
    private Long orderId;

    private Long customerID;

    private List<OrderItemEvent> items;

    private AddressEvent address;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public List<OrderItemEvent> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEvent> items) {
        this.items = items;
    }

    public AddressEvent getAddress() {
        return address;
    }

    public void setAddress(AddressEvent address) {
        this.address = address;
    }
}
