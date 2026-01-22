package org.example.nirsshop.mapper;

import org.example.nirsshop.model.Store;
import org.example.nirsshop.model.createdto.StoreCreateDto;
import org.example.nirsshop.model.dto.StoreDto;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper implements Mapper<Store, StoreDto, StoreCreateDto> {

    @Override
    public StoreDto toDto(Store entity) {
        if (entity == null) return null;
        return new StoreDto(
                entity.getStoreId(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getRent(),
                entity.getRating()
        );
    }

    @Override
    public Store fromCreateDto(StoreCreateDto createDto) {
        if (createDto == null) return null;
        return Store.builder()
                .address(createDto.address())
                .phone(createDto.phone())
                .rent(createDto.rent())
                .rating(createDto.rating())
                .build();
    }
}

