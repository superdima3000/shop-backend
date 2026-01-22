package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.StoreMapper;
import org.example.nirsshop.model.Store;
import org.example.nirsshop.model.createdto.StoreCreateDto;
import org.example.nirsshop.model.dto.StoreDto;
import org.example.nirsshop.repository.StoreRepository;
import org.example.nirsshop.service.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    @Override
    public List<StoreDto> findAll() {
        return storeRepository.findAll()
                .stream()
                .map(storeMapper::toDto)
                .toList();
    }

    @Override
    public StoreDto findById(Integer id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Store not found: " + id));
        return storeMapper.toDto(store);
    }

    @Override
    public StoreDto create(StoreCreateDto createDto) {
        Store store = storeMapper.fromCreateDto(createDto);
        Store saved = storeRepository.save(store);
        return storeMapper.toDto(saved);
    }

    @Override
    public StoreDto update(Integer id, StoreCreateDto createDto) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Store not found: " + id));

        store.setAddress(createDto.address());
        store.setPhone(createDto.phone());
        store.setRent(createDto.rent());
        store.setRating(createDto.rating());

        Store saved = storeRepository.save(store);
        return storeMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        if (!storeRepository.existsById(id)) {
            throw new NotFoundException("Store not found: " + id);
        }
        storeRepository.deleteById(id);
    }
}

