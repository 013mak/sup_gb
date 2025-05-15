package com.example.projectmanager;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.projectmanager.model.Role;
import com.example.projectmanager.model.User;
import com.example.projectmanager.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Set;

@SpringBootApplication
public class ProjectManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectManagerApplication.class, args);
    }

    /*
    создание первого пользователя администратора
     */
    @Bean
    public CommandLineRunner run(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userService.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .roles(Set.of(Role.ROLE_ADMIN))
                        .build();

                userService.save(admin);
                System.out.println("Первый администратор был успешно создан!");
            }
        };
    }
}