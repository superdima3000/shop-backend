package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.OrderItemMapper;
import org.example.nirsshop.model.Order;
import org.example.nirsshop.model.Product;
import org.example.nirsshop.model.ProductOrder;
import org.example.nirsshop.model.ProductOrderId;
import org.example.nirsshop.model.createdto.OrderItemCreateDto;
import org.example.nirsshop.model.dto.OrderItemDto;
import org.example.nirsshop.repository.OrderRepository;
import org.example.nirsshop.repository.ProductOrderRepository;
import org.example.nirsshop.repository.ProductRepository;
import org.example.nirsshop.service.ProductOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductOrderRepository productOrderRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemDto> findAll() {
        return productOrderRepository.findAll()
                .stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto findById(Integer productId, Integer orderId) {
        ProductOrder productOrder = productOrderRepository.findByProductIdAndOrderId(productId, orderId)
                .orElseThrow(() -> new NotFoundException("Product order not found: productId=" + productId + ", orderId=" + orderId));
        return orderItemMapper.toDto(productOrder);
    }

    @Override
    public OrderItemDto create(OrderItemCreateDto createDto) {
        Product product = productRepository.findById(createDto.productId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + createDto.productId()));

        Order order = orderRepository.findById(createDto.orderId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + createDto.orderId()));

        ProductOrder productOrder = orderItemMapper.fromCreateDto(createDto);
        productOrder.setProduct(product);
        productOrder.setOrder(order);

        ProductOrder saved = productOrderRepository.save(productOrder);
        return orderItemMapper.toDto(saved);
    }

    @Override
    public OrderItemDto update(Integer productId, Integer orderId, OrderItemCreateDto createDto) {
        ProductOrder productOrder = productOrderRepository.findByProductIdAndOrderId(productId, orderId)
                .orElseThrow(() -> new NotFoundException("Product order not found: productId=" + productId + ", orderId=" + orderId));

        productOrder.setQuantity(createDto.quantity());

        ProductOrder saved = productOrderRepository.save(productOrder);
        return orderItemMapper.toDto(saved);
    }

    @Override
    public void delete(Integer productId, Integer orderId) {
        if (!productOrderRepository.existsByProductIdAndOrderId(productId, orderId)) {
            throw new NotFoundException("Product order not found: productId=" + productId + ", orderId=" + orderId);
        }
        productOrderRepository.deleteByProductIdAndOrderId(productId, orderId);
    }

    @Override
    public List<OrderItemDto> findByOrderId(Integer orderId) {
        List<ProductOrder> productOrders = productOrderRepository.findByOrderId(orderId);
        if (productOrders.isEmpty()) {
            throw new NotFoundException("Product order not found: orderId=" + orderId);
        }

        return productOrders.stream()
                .map(orderItemMapper::toDto)
                .toList();
    }
}
