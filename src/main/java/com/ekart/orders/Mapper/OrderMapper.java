package com.ekart.orders.Mapper;

import com.ekart.orders.dto.AddressDTO;
import com.ekart.orders.dto.OrdersRequestDTO;
import com.ekart.orders.entity.Orders;
import com.ekart.orders.event.AddressEvent;
import com.ekart.orders.event.OrderItemEvent;
import com.ekart.orders.event.OrderPlacedEvent;

public class OrderMapper {

    public static OrderPlacedEvent toEvent(Orders orders, AddressDTO addressDTO) {

        OrderPlacedEvent event = new OrderPlacedEvent();

        event.setOrderId(orders.getOrderId());
        event.setCustomerID(orders.getCustomerId());

        if (orders.getOrderItems() != null) {
            event.setItems(orders.getOrderItems().stream().map(item -> {
                OrderItemEvent e = new OrderItemEvent();
                e.setProductId(item.getProductId());
                e.setQuantity(item.getQuantity());
                return e;
            }).toList());
        }
            if (addressDTO != null) {
                AddressEvent addressEvent = new AddressEvent();
                addressEvent.setArea(addressDTO.getArea());
                addressEvent.setCity(addressDTO.getCity());
                addressEvent.setPincode(addressDTO.getPincode());
                addressEvent.setState(addressDTO.getState());
                addressEvent.setHouseNumber(addressDTO.getHouseNumber());
                event.setAddress(addressEvent);
            }
            return event;
        }

}
