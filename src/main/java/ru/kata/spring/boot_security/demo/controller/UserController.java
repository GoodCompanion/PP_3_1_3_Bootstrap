package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/user/profile")
    public String profilePage(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", currentUser);
        model.addAttribute("isOwner", true);
        return "layout";
    }

    @GetMapping("/user/profile-content")
    public String profileContent(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", currentUser);
        model.addAttribute("isOwner", true);
        return "profile :: content";
    }

    @GetMapping("/user/users")
    public String usersList(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", currentUser);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("viewMode", "user");
        model.addAttribute("isOwner", true);
        return "layout";
    }

    @GetMapping("/user/users-content")
    public String usersContent(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("user", currentUser);
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("viewMode", "user");
        return "users :: content";
    }
}
