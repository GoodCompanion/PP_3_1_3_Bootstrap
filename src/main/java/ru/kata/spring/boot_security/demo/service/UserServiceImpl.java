package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dto.CreateUserRequest;
import ru.kata.spring.boot_security.demo.dto.UpdateUserRequest;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleDao;
import ru.kata.spring.boot_security.demo.repository.UserDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createUser(CreateUserRequest request) {
        validateUsernameUniquenessForCreation(request.getUsername());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setAge(request.getAge());

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleDao.findAllById(request.getRoleIds()));
            user.setRoles(roles);
        }

        userDao.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userDao.findByIdWithRoles(id).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.findByUsernameWithRoles(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAllWithRoles();
    }

    @Override
    @Transactional
    public void updateUser(UpdateUserRequest request) {
        User user = userDao.findByIdWithRoles(request.getId()).orElseThrow(RuntimeException::new);
        validateUsernameUniqueness(request.getUsername(), user.getId());

        user.setUsername(request.getUsername());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setAge(request.getAge());

        if (request.getRoleIds() != null) {
            if (request.getRoleIds().isEmpty()) {
                user.setRoles(new HashSet<>());
            } else {
                Set<Role> roles = new HashSet<>(roleDao.findAllById(request.getRoleIds()));
                user.setRoles(roles);
            }
        }

        userDao.save(user);
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id, String currentUsername) {
        User userToDelete = userDao.findByIdWithRoles(id).orElse(null);
        if (userToDelete == null) {
            return false;
        }
        userDao.deleteById(id);
        return userToDelete.getUsername().equals(currentUsername);
    }

    private void validateUsernameUniqueness(String username, Long currentId) {
        userDao.findByUsername(username).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(currentId)) {
                throw new RuntimeException("Username is already taken");
            }
        });
    }

    private void validateUsernameUniquenessForCreation(String username) {
        userDao.findByUsername(username).ifPresent(existingUser -> {
            throw new RuntimeException("Username is already taken");
        });
    }

    @Override
    public CreateUserRequest convertToCreateRequest(User user) {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(user.getUsername());
        request.setName(user.getName());
        request.setSurname(user.getSurname());
        request.setAge(user.getAge());

        if (user.getRoles() != null) {
            request.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()));
        }
        return request;
    }

    @Override
    public UpdateUserRequest convertToUpdateRequest(User user) {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setId(user.getId());
        request.setUsername(user.getUsername());
        request.setName(user.getName());
        request.setSurname(user.getSurname());
        request.setAge(user.getAge());

        if (user.getRoles() != null) {
            request.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()));
        }
        return request;
    }
}
