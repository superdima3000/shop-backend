package org.example.nirsshop.service;

import org.example.nirsshop.model.User;
import org.example.nirsshop.model.createdto.UserCreateDto;
import org.example.nirsshop.model.dto.UserDto;

public interface UserService extends CrudService<UserDto, UserCreateDto, Integer> {
    UserDto findByEmail(String email);
    User getEntityByEmail(String email);
}
