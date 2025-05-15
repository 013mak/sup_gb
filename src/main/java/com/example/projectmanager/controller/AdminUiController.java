package com.example.projectmanager.controller;

import com.example.projectmanager.model.Role;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminUiController {

    private final UserRepository userRepository;

    public AdminUiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", Role.values());
        return "users";
    }

    /*
    назначение прав доступа пользователям
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/set-role")
    public String setUserRole(@RequestParam Long userId,
                              @RequestParam(name = "roles", required = false) List<Role> roles) {
        User user = userRepository.findById(userId).orElseThrow();

        if (roles != null && !roles.isEmpty()) {
            user.setRoles(new HashSet<>(roles));
            userRepository.save(user);
        }
        return "redirect:/admin/users";
    }
}