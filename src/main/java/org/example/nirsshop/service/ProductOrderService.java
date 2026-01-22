package org.example.nirsshop.service;

import org.example.nirsshop.model.createdto.OrderItemCreateDto;
import org.example.nirsshop.model.dto.OrderItemDto;

import java.util.List;

public interface ProductOrderService {
    List<OrderItemDto> findAll();
    OrderItemDto findById(Integer productId, Integer orderId);
    OrderItemDto create(OrderItemCreateDto createDto);
    OrderItemDto update(Integer productId, Integer orderId, OrderItemCreateDto createDto);
    void delete(Integer productId, Integer orderId);

    List<OrderItemDto> findByOrderId(Integer orderId);
}

