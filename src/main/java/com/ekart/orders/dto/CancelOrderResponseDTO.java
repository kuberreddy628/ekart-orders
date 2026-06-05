package com.ekart.orders.dto;

import java.util.List;

public class CancelOrderResponseDTO {

   // private OrdersResponseDTO ordersResponseDTO;
    private Long orderId;
    private String status;
    private List<CancelProductsDTO> releasedItems;

   /* public OrdersResponseDTO getOrdersResponseDTO() {
        return ordersResponseDTO;
    }

    public void setOrdersResponseDTO(OrdersResponseDTO ordersResponseDTO) {
        this.ordersResponseDTO = ordersResponseDTO;
    }*/

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CancelProductsDTO> getReleasedItems() {
        return releasedItems;
    }

    public void setReleasedItems(List<CancelProductsDTO> releasedItems) {
        this.releasedItems = releasedItems;
    }
}
