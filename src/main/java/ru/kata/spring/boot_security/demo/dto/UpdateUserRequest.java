package ru.kata.spring.boot_security.demo.dto;

import javax.validation.constraints.*;
import java.util.Set;

public class UpdateUserRequest {
    @NotNull
    private Long id;

    @NotBlank(message = "Логин обязателен")
    @Size(min = 1, max = 50)
    private String username;

    @Size(max = 50)
    private String password;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 1)
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 1)
    private String surname;

    @Min(0)
    @Max(150)
    private int age;

    private Set<Long> roleIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
