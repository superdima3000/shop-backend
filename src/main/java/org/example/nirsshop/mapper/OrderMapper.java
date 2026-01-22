package org.example.nirsshop.mapper;

import org.example.nirsshop.model.Order;
import org.example.nirsshop.model.createdto.OrderCreateDto;
import org.example.nirsshop.model.dto.OrderDto;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper implements Mapper<Order, OrderDto, OrderCreateDto> {

    @Override
    public OrderDto toDto(Order entity) {
        if (entity == null) return null;
        Integer customerId = entity.getCustomer() != null ? entity.getCustomer().getCustomerId() : null;
        return new OrderDto(
                entity.getOrderId(),
                entity.getOrderDate(),
                entity.getOrderStatus(),
                entity.getTotalAmount(),
                entity.getWeight(),
                entity.getItemCount(),
                customerId,
                entity.getIsPaid()
        );
    }

    @Override
    public Order fromCreateDto(OrderCreateDto createDto) {
        if (createDto == null) return null;
        // customer проставляется в сервисе
        return Order.builder()
                .orderDate(createDto.orderDate())
                .orderStatus(createDto.orderStatus())
                .totalAmount(createDto.totalAmount())
                .weight(createDto.weight())
                .itemCount(createDto.itemCount())
                .isPaid(createDto.isPaid())
                .build();
    }
}

