package com.example.projectmanager.controller;


import com.example.projectmanager.config.SecurityConfig;
import com.example.projectmanager.controller.AdminUiController;
import com.example.projectmanager.model.Role;
import com.example.projectmanager.model.User;
import com.example.projectmanager.service.CustomUserDetailsService;
import com.example.projectmanager.service.ProjectService;
import com.example.projectmanager.service.TaskService;
import com.example.projectmanager.service.UserService;
import com.example.projectmanager.util.JwtUtil;
import com.example.projectmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUiController.class)
@Import(SecurityConfig.class)
class AdminUiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testListUsers() throws Exception {
        List<User> users = List.of(new User());
        Mockito.when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("roles"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSetUserRole() throws Exception {
        User user = new User();
        user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/admin/set-role")
                        .param("userId", "1")
                        .param("roles", "ROLE_ADMIN", "ROLE_MANAGER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        Mockito.verify(userRepository).save(Mockito.argThat(u ->
                u.getRoles().containsAll(Arrays.asList(Role.ROLE_ADMIN, Role.ROLE_MANAGER))));
    }

    @Test
    @WithMockUser(roles = "USER") // Без прав администратора
    void testListUsersForbidden() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testSetUserRoleForbidden() throws Exception {
        mockMvc.perform(post("/admin/set-role")
                        .param("userId", "1")
                        .param("roles", "ADMIN"))
                .andExpect(status().isForbidden());
    }
}