package org.example.nirsshop.mapper;

import org.example.nirsshop.model.ProductOrder;
import org.example.nirsshop.model.ProductOrderId;
import org.example.nirsshop.model.createdto.OrderItemCreateDto;
import org.example.nirsshop.model.dto.OrderItemDto;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper implements Mapper<ProductOrder, OrderItemDto, OrderItemCreateDto> {

    @Override
    public OrderItemDto toDto(ProductOrder entity) {
        if (entity == null) return null;
        return new OrderItemDto(
                entity.getProduct().getProductId(),
                entity.getOrder().getOrderId(),
                entity.getQuantity()
        );
    }

    @Override
    public ProductOrder fromCreateDto(OrderItemCreateDto createDto) {
        if (createDto == null) return null;
        ProductOrder po = new ProductOrder();
        ProductOrderId id = new ProductOrderId();
        id.setProductId(createDto.productId());
        id.setOrderId(createDto.orderId());
        po.setId(id);
        po.setQuantity(createDto.quantity());
        // Product и Order подставляются в сервисе
        return po;
    }
}

