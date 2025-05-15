package com.example.projectmanager.controller;


import com.example.projectmanager.config.SecurityConfig;
import com.example.projectmanager.filter.JwtAuthenticationFilter;
import com.example.projectmanager.model.Role;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.UserRepository;
import com.example.projectmanager.service.CustomUserDetailsService;
import com.example.projectmanager.service.UserService;
import com.example.projectmanager.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthUiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserService userService;

    @Test
    void loginForm_ReturnsLoginView() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void registerForm_ReturnsRegisterView() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void register_NewUser_SuccessRedirectsToProjects() throws Exception {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encoded");

        mockMvc.perform(post("/auth/register")
                        .param("username", "test")
                        .param("password", "password")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getUsername()).isEqualTo("test");
        assertThat(saved.getPassword()).isEqualTo("encoded");
        assertThat(saved.getRoles()).containsExactly(Role.ROLE_USER);
    }

    @Test

    void register_ExistingUser_RedirectsWithError() throws Exception {
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/auth/register")
                        .param("username", "test")
                        .param("password", "pass")
                        .with(csrf())) // ⬅️ добавь это!
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/register"))
                .andExpect(flash().attribute("error", "Пользователь с таким именем уже существует."));
    }
}