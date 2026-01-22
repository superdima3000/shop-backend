package org.example.nirsshop;

import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.Store;
import org.example.nirsshop.model.createdto.StoreCreateDto;
import org.example.nirsshop.model.dto.StoreDto;
import org.example.nirsshop.repository.StoreRepository;
import org.example.nirsshop.service.StoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class StoreServiceImplIT {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void findAll_ReturnsAllStores() {
        // Arrange
        Store store1 = createStore("Main Street 1", "+111111111", 50000);
        Store store2 = createStore("Second Street 2", "+222222222", 40000);
        storeRepository.saveAll(List.of(store1, store2));

        // Act
        List<StoreDto> result = storeService.findAll();

        // Assert
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_ExistingStore_ReturnsStoreDto() {
        // Arrange
        Store store = createStore("Downtown Plaza", "+333333333", 60000);
        Store saved = storeRepository.save(store);

        // Act
        StoreDto result = storeService.findById(saved.getStoreId());

        // Assert
        assertNotNull(result);
        assertEquals("Downtown Plaza", result.address());
        assertEquals("+333333333", result.phone());
        assertEquals(BigDecimal.valueOf(60000), result.rent());
    }

    @Test
    void findById_NonExistingStore_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> storeService.findById(9999)
        );
        assertTrue(exception.getMessage().contains("Store not found"));
    }

    @Test
    void create_CreatesStore() {
        // Arrange
        StoreCreateDto createDto = new StoreCreateDto(
                "Shopping Mall A",
                "+444444444",
                BigDecimal.valueOf(75000),
                4.0
        );

        // Act
        StoreDto result = storeService.create(createDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Shopping Mall A", result.address());
        assertEquals("+444444444", result.phone());
        assertEquals(BigDecimal.valueOf(75000), result.rent());

        Store savedStore = storeRepository.findById(result.id()).orElseThrow();
        assertEquals("Shopping Mall A", savedStore.getAddress());
        assertEquals(BigDecimal.valueOf(75000), savedStore.getRent());
    }

    @Test
    void update_ExistingStore_UpdatesSuccessfully() {
        // Arrange
        Store store = createStore("Old Address", "+666666666", 30000);
        Store saved = storeRepository.save(store);

        StoreCreateDto updateDto = new StoreCreateDto(
                "New Address",
                "+777777777",
                BigDecimal.valueOf(80000),
                4.5
        );

        // Act
        StoreDto result = storeService.update(saved.getStoreId(), updateDto);

        // Assert
        assertEquals("New Address", result.address());
        assertEquals("+777777777", result.phone());
        assertEquals(BigDecimal.valueOf(80000), result.rent());

        Store updatedStore = storeRepository.findById(saved.getStoreId()).orElseThrow();
        assertEquals("New Address", updatedStore.getAddress());
        assertEquals(BigDecimal.valueOf(80000), updatedStore.getRent());
    }

    @Test
    void update_SetRentToNull_UpdatesSuccessfully() {
        // Arrange
        Store store = createStore("Store Address", "+888888888", 50000);
        Store saved = storeRepository.save(store);

        StoreCreateDto updateDto = new StoreCreateDto(
                "Store Address",
                "+888888888",
                null,
                4.6
        );

        // Act
        StoreDto result = storeService.update(saved.getStoreId(), updateDto);

        // Assert
        assertNull(result.rent());

        Store updatedStore = storeRepository.findById(saved.getStoreId()).orElseThrow();
        assertNull(updatedStore.getRent());
    }

    @Test
    void update_NonExistingStore_ThrowsNotFoundException() {
        // Arrange
        StoreCreateDto updateDto = new StoreCreateDto(
                "Address",
                "+000000000",
                BigDecimal.valueOf(50000),
                4.7
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> storeService.update(9999, updateDto)
        );
        assertTrue(exception.getMessage().contains("Store not found"));
    }

    @Test
    void delete_ExistingStore_DeletesSuccessfully() {
        // Arrange
        Store store = createStore("To Delete", "+999999999", 45000);
        Store saved = storeRepository.save(store);
        Integer storeId = saved.getStoreId();

        // Act
        storeService.delete(storeId);

        // Assert
        assertFalse(storeRepository.existsById(storeId));
    }

    @Test
    void delete_NonExistingStore_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> storeService.delete(9999)
        );
        assertTrue(exception.getMessage().contains("Store not found"));
    }

    private Store createStore(String address, String phone, Integer rent) {
        Store store = new Store();
        store.setAddress(address);
        store.setPhone(phone);
        store.setRent(BigDecimal.valueOf(rent));
        return store;
    }
}

