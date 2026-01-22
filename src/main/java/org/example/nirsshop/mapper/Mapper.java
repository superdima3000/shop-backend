package org.example.nirsshop.mapper;

public interface Mapper<E, D, C> {

    D toDto(E entity);

    E fromCreateDto(C createDto);
}

