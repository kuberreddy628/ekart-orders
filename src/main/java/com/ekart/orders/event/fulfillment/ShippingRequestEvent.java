package com.ekart.orders.event.fulfillment;

public class ShippingRequestEvent {

    private Long orderId;
    private ShippingAddressPayload address;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public ShippingAddressPayload getAddress() {
        return address;
    }

    public void setAddress(ShippingAddressPayload address) {
        this.address = address;
    }
}
