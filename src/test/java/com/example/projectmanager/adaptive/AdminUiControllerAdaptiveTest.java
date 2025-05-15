package com.example.projectmanager.adaptive;


import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminUiControllerAdaptiveTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testViewUserListAsAdmin() throws Exception {
        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testViewUserListAsNonAdminForbidden() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testViewUserProfileNotFound() throws Exception {
        Mockito.when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/users/999"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testViewUserProfileExists() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/admin/employees/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/employee-view"))
                .andExpect(model().attributeExists("details"));
    }
}