package org.example.nirsshop.model.createdto;

public record ProductCreateDto(
        String name,
        String article,
        Integer price,
        Integer weight,
        String description,
        String gender,
        Integer categoryId,
        Double rating
) {}

