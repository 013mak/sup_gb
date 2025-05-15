package com.example.projectmanager.adaptive;

import com.example.projectmanager.model.Role;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserFlowAdaptiveTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private UserRepository userRepository;


    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void userLoginProjectViewTaskCreateLogoutFlow() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/list"));

        mockMvc.perform(get("/tasks/create/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/create"));

        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void adminEditUserAssignRoleAndAccessCheck() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/admin/employees/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/employee-edit"));

        mockMvc.perform(post("/admin/set-role")
                        .param("userId", "1")
                        .param("roles", "ROLE_MANAGER")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void projectCreateAndAssignUserToTaskFlow() throws Exception {
        mockMvc.perform(get("/projects/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/create"));

        mockMvc.perform(post("/projects/create")
                        .param("name", "Test Project")
                        .param("description", "Project Description")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/projects/**"));

        mockMvc.perform(post("/tasks/create")
                        .param("title", "Task 1")
                        .param("projectId", "1")
                        .param("assignee.id", "1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));
    }
}

