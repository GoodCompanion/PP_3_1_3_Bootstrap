package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.CreateUserRequest;
import ru.kata.spring.boot_security.demo.dto.UpdateUserRequest;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {

    void createUser(CreateUserRequest request);

    User getUserById(Long id);
    User getUserByUsername(String username);
    List<User> getAllUsers();

    void updateUser(UpdateUserRequest request);

    void deleteUser(Long id);
}
