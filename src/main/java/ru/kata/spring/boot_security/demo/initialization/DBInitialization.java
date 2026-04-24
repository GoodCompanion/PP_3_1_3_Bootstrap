package ru.kata.spring.boot_security.demo.initialization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleDao;
import ru.kata.spring.boot_security.demo.repository.UserDao;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
public class DBInitialization {
    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DBInitialization(UserDao userDao, RoleDao roleDao, PasswordEncoder encoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = encoder;
    }

    // временные пользователи и роли для теста
    @PostConstruct
    @Transactional
    public void init() {
        if (userDao.count() == 0) {
            Role adminRole = new Role("ROLE_ADMIN");
            Role userRole = new Role("ROLE_USER");

            if (roleDao.count() == 0) {
                roleDao.save(adminRole);
                roleDao.save(userRole);
            }

            User admin = new User("admin", "admin", "Владимир", "Калинин", 25, Set.of(adminRole, userRole));
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            User user = new User("user", "user", "Эрик", "Цой", 24, Set.of(userRole));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDao.save(admin);
            userDao.save(user);
        }
    }
}
