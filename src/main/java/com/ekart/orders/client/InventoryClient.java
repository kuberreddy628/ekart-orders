package com.ekart.orders.client;

import com.ekart.orders.dto.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory", url = "${inventory.service.url}")
public interface InventoryClient {

    @PostMapping("/reservations/reserveProduct")
    ReservationResponseDTO reserveOrder(@RequestBody OrderPlacementRequestDTO placementRequestDTO);

    @GetMapping("/Inventory/productByID/{productID}")
    ProductDetailsDTO getProductByID(@PathVariable("productID") Long productID);

    @PostMapping("/reservations/release/{orderId}")
    CancelOrderResponseDTO cancelOrder(@PathVariable("orderId") Long orderId);

}
