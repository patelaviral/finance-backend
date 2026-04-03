package com.aviral.finance_backend.controller;

import com.aviral.finance_backend.model.User;
import com.aviral.finance_backend.model.enums.Role;
import com.aviral.finance_backend.model.enums.Status;
import com.aviral.finance_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping
    public List<User> getAllUsers(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        return userService.getAllUsers(user);
    }

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody User user,
            HttpServletRequest request) {

        User usr = (User) request.getAttribute("user");
        return userService.updateUser(id, user, usr);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id, HttpServletRequest request) {

        User user = (User) request.getAttribute("user");
        userService.deleteUser(id, user);
        return "User deleted successfully";
    }

    @PutMapping("/{id}/role")
    public User updateUserRole(
            @PathVariable Long id,
            @RequestParam Role role,
            HttpServletRequest request) {

        User user = (User) request.getAttribute("user");
        return userService.updateUserRole(id, role, user);
    }

    @PutMapping("/{id}/status")
    public User updateUserStatus(
            @PathVariable Long id,
            @RequestParam Status status,
            HttpServletRequest request) {

        User user = (User) request.getAttribute("user");
        return userService.updateUserStatus(id, status, user);
    }
}
