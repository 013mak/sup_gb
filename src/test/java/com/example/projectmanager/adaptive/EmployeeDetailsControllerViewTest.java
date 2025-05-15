package com.example.projectmanager.adaptive;


import com.example.projectmanager.controller.EmployeeDetailsController;
import com.example.projectmanager.model.EmployeeDetails;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.EmployeeDetailsRepository;
import com.example.projectmanager.repository.UserRepository;
import com.example.projectmanager.service.CustomUserDetailsService;
import com.example.projectmanager.service.UserService;
import com.example.projectmanager.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = EmployeeDetailsController.class, excludeAutoConfiguration = ThymeleafAutoConfiguration.class)
@WithMockUser(roles = "ADMIN")
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeDetailsControllerViewTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private EmployeeDetailsRepository employeeDetailsRepository;

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("GET /admin/employees/{userId}")
    class ViewEmployeeDetailsTests {

        @Test
        @DisplayName("View existing employee details")
        void testViewEmployeeDetails_whenDetailsExist() throws Exception {
            User user = new User();
            user.setId(1L);
            EmployeeDetails details = new EmployeeDetails();
            details.setId(1L);
            user.setEmployeeDetails(details);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            mockMvc.perform(get("/admin/employees/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/employee-view"))
                    .andExpect(model().attribute("details", hasProperty("id", is(1L))));
        }

        @Test
        @DisplayName("View employee without details")
        void testViewEmployeeDetails_whenNoDetails() throws Exception {
            User user = new User();
            user.setId(2L);
            user.setEmployeeDetails(null);

            when(userRepository.findById(2L)).thenReturn(Optional.of(user));

            mockMvc.perform(get("/admin/employees/2"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/employee-view"))
                    .andExpect(model().attribute("details", hasProperty("firstName", nullValue())));
        }

        @Test
        @DisplayName("View non-existing user returns 404")
        void testViewEmployeeDetails_whenUserNotFound() throws Exception {
            when(userRepository.findById(3L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/admin/employees/3"))
                    .andExpect(status().isNotFound());
        }
    }

}

