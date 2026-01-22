package org.example.nirsshop.service;

import org.example.nirsshop.model.createdto.OrderCreateDto;
import org.example.nirsshop.model.dto.OrderDto;

import java.util.List;

public interface OrderService extends CrudService<OrderDto, OrderCreateDto, Integer> {
    List<OrderDto> findByCustomerId(Integer customerId);
}

