package org.example.nirsshop.service;

import java.util.List;

public interface CrudService<D, C, ID> {

    List<D> findAll();

    D findById(ID id);

    D create(C createDto);

    D update(ID id, C createDto);

    void delete(ID id);
}

