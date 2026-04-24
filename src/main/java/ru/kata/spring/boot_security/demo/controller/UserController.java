package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kata.spring.boot_security.demo.configs.SuccessUserHandler;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
public class UserController {

    UserService userService;
    SuccessUserHandler successUserHandler;

    @Autowired
    public UserController(UserService userService, SuccessUserHandler successUserHandler) {
        this.userService = userService;
        this.successUserHandler = successUserHandler;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        return "redirect:" + successUserHandler.determineTargetUrl(authentication);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("#username == authentication.name or hasAuthority('ROLE_ADMIN')")
    public String userPage(@PathVariable("username") String username, Model model) {
        try {
            User user = userService.getUserByUsername(username);
            model.addAttribute("user", user);
            return "user";
        } catch (UsernameNotFoundException e) {
            return "redirect:/";
        }
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
