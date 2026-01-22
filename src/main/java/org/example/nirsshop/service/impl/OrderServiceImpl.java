package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.OrderMapper;
import org.example.nirsshop.model.Customer;
import org.example.nirsshop.model.Order;
import org.example.nirsshop.model.createdto.OrderCreateDto;
import org.example.nirsshop.model.dto.OrderDto;
import org.example.nirsshop.repository.CustomerRepository;
import org.example.nirsshop.repository.OrderRepository;
import org.example.nirsshop.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto findById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto create(OrderCreateDto createDto) {
        Order order = orderMapper.fromCreateDto(createDto);

        Customer customer = customerRepository.findById(createDto.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + createDto.customerId()));
        order.setCustomer(customer);

        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Override
    public OrderDto update(Integer id, OrderCreateDto createDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));

        order.setOrderDate(createDto.orderDate());
        order.setOrderStatus(createDto.orderStatus());
        order.setIsPaid(createDto.isPaid());
        order.setTotalAmount(createDto.totalAmount());
        order.setWeight(createDto.weight());
        order.setItemCount(createDto.itemCount());

        Customer customer = customerRepository.findById(createDto.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + createDto.customerId()));
        order.setCustomer(customer);

        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Override
    public List<OrderDto> findByCustomerId(Integer customerId) {
        return orderRepository.findByCustomer_CustomerId(customerId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }
}

