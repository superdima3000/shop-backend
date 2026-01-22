package org.example.nirsshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.model.createdto.OrderItemCreateDto;
import org.example.nirsshop.model.dto.OrderItemDto;
import org.example.nirsshop.service.ProductOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class ProductOrderController {

    private final ProductOrderService productOrderService;

    @GetMapping
    public ResponseEntity<List<OrderItemDto>> getAllOrderItems() {
        List<OrderItemDto> items = productOrderService.findAll();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/product/{productId}/order/{orderId}")
    public ResponseEntity<OrderItemDto> getOrderItem(
            @PathVariable Integer productId,
            @PathVariable Integer orderId) {
        OrderItemDto item = productOrderService.findById(productId, orderId);
        return ResponseEntity.ok(item);
    }

    @GetMapping("order/{orderId}")
    public ResponseEntity<List<OrderItemDto>> getOrderItem(
            @PathVariable Integer orderId) {
        List<OrderItemDto> item = productOrderService.findByOrderId(orderId);
        return ResponseEntity.ok(item);
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> createOrderItem(@RequestBody OrderItemCreateDto createDto) {
        OrderItemDto created = productOrderService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/product/{productId}/order/{orderId}")
    public ResponseEntity<OrderItemDto> updateOrderItem(
            @PathVariable Integer productId,
            @PathVariable Integer orderId,
            @RequestBody OrderItemCreateDto createDto) {
        OrderItemDto updated = productOrderService.update(productId, orderId, createDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/product/{productId}/order/{orderId}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable Integer productId,
            @PathVariable Integer orderId) {
        productOrderService.delete(productId, orderId);
        return ResponseEntity.noContent().build();
    }
}
