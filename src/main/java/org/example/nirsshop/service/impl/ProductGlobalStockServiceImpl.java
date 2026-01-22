package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.ProductGlobalStockMapper;
import org.example.nirsshop.model.Product;
import org.example.nirsshop.model.ProductGlobalStock;
import org.example.nirsshop.model.createdto.ProductGlobalStockCreateDto;
import org.example.nirsshop.model.dto.ProductGlobalStockDto;
import org.example.nirsshop.repository.ProductGlobalStockRepository;
import org.example.nirsshop.repository.ProductRepository;
import org.example.nirsshop.service.ProductGlobalStockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductGlobalStockServiceImpl implements ProductGlobalStockService {

    private final ProductGlobalStockRepository productGlobalStockRepository;
    private final ProductRepository productRepository;
    private final ProductGlobalStockMapper productGlobalStockMapper;

    @Override
    public List<ProductGlobalStockDto> findAll() {
        return productGlobalStockRepository.findAll()
                .stream()
                .map(productGlobalStockMapper::toDto)
                .toList();
    }

    @Override
    public ProductGlobalStockDto findById(Integer id) {
        ProductGlobalStock stock = productGlobalStockRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product global stock not found: " + id));
        return productGlobalStockMapper.toDto(stock);
    }

    @Override
    public ProductGlobalStockDto create(ProductGlobalStockCreateDto createDto) {
        Product product = productRepository.findById(createDto.productId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + createDto.productId()));

        ProductGlobalStock stock = productGlobalStockMapper.fromCreateDto(createDto);
        stock.setProduct(product);

        ProductGlobalStock saved = productGlobalStockRepository.save(stock);
        return productGlobalStockMapper.toDto(saved);
    }

    @Override
    public ProductGlobalStockDto update(Integer id, ProductGlobalStockCreateDto createDto) {
        Optional<ProductGlobalStock> existingStock = productGlobalStockRepository.findById(id);

        if (existingStock.isEmpty()) {
            return create(createDto);
        }

        ProductGlobalStock stock = existingStock.get();
        stock.setQuantity(createDto.quantity());
        stock.setPrice(createDto.price());

        ProductGlobalStock saved = productGlobalStockRepository.save(stock);
        return productGlobalStockMapper.toDto(saved);
    }


    @Override
    public void delete(Integer id) {
        if (!productGlobalStockRepository.existsById(id)) {
            throw new NotFoundException("Product global stock not found: " + id);
        }
        productGlobalStockRepository.deleteById(id);
    }
}

