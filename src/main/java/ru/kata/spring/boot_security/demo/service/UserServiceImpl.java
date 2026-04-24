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
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }
}
