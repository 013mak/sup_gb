package com.example.projectmanager.controller;

import com.example.projectmanager.model.Role;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@RequestMapping("/auth")
public class AuthUiController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthUiController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
    страница входа
     */
    @GetMapping("/login")
    public String loginForm(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/projects";
        }
        return "login";
    }
    /*
    регистрация с проверкой на существующий логин
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Пользователь с таким именем уже существует.");
            return "redirect:/auth/register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Регистрация прошла успешно! Войдите в систему.");
        return "redirect:/projects";
    }
}