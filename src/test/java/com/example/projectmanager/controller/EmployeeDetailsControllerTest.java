package com.example.projectmanager.controller;


import com.example.projectmanager.config.SecurityConfig;
import com.example.projectmanager.filter.JwtAuthenticationFilter;
import com.example.projectmanager.model.EmployeeDetails;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.EmployeeDetailsRepository;
import com.example.projectmanager.repository.UserRepository;
import com.example.projectmanager.service.CustomUserDetailsService;
import com.example.projectmanager.service.UserService;
import com.example.projectmanager.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "admin", roles = "ADMIN")
class EmployeeDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeDetailsRepository employeeDetailsRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testEditEmployeeDetailsForm() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmployeeDetails(null);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/admin/employees/edit/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/employee-edit"))
                .andExpect(model().attributeExists("details"));
    }

    @Test
    void testSaveEmployeeDetails() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        EmployeeDetails details = new EmployeeDetails();
        details.setUser(user);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(post("/admin/employees/edit")
                        .param("user.id", userId.toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        Mockito.verify(employeeDetailsRepository).save(Mockito.any(EmployeeDetails.class));
    }

    @Test
    void testViewEmployeeDetails() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmployeeDetails(null);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/admin/employees/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/employee-view"))
                .andExpect(model().attributeExists("details"));
    }

}
