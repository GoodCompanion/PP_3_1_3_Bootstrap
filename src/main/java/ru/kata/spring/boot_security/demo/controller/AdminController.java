package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.CreateUserRequest;
import ru.kata.spring.boot_security.demo.dto.UpdateUserRequest;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String adminPage(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("viewMode", "admin");
        return "layout";
    }

    @GetMapping("/users-content")
    public String usersContent(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("viewMode", "admin");
        return "users :: content";
    }

    @GetMapping("/users/add-content")
    public String addUserContent(Model model) {
        model.addAttribute("user", new CreateUserRequest());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "add :: content";
    }

    @PostMapping("/users/add")
    public String addUser(@Valid @ModelAttribute("user") CreateUserRequest request,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "add :: content";
        }
        userService.createUser(request);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
        UpdateUserRequest request = userService.convertToUpdateRequest(user);
        model.addAttribute("user", request);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "edit";
    }

    @PostMapping("/users/edit")
    public String editUser(@Valid @ModelAttribute("user") UpdateUserRequest request,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "edit :: .edit-form-container";
        }
        userService.updateUser(request);
        return "edit :: .edit-form-container";
    }

    @PostMapping("/users/delete/{id}")
    @ResponseBody
    public String deleteUser(@PathVariable Long id,
                             Authentication authentication,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        if (userService.deleteUser(id, authentication.getName())) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return "self-deleted";
        }
        return "success";
    }
}

