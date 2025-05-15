package com.example.projectmanager.controller;


import com.example.projectmanager.config.SecurityConfig;
import com.example.projectmanager.filter.JwtAuthenticationFilter;
import com.example.projectmanager.model.Project;
import com.example.projectmanager.model.Task;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.CommentRepository;
import com.example.projectmanager.repository.TaskRepository;
import com.example.projectmanager.service.ProjectService;
import com.example.projectmanager.service.TaskService;
import com.example.projectmanager.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private PasswordEncoder passwordEncoder;


    @MockBean
    private TaskService taskService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser
    void testCreateForm() throws Exception {
        Project project = new Project();
        project.setId(1L);
        Mockito.when(projectService.findById(1L)).thenReturn(Optional.of(project));

        mockMvc.perform(get("/tasks/create/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/create"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attribute("projectId", 1L));
    }

    @Test
    @WithMockUser
    void testCreateTask() throws Exception {
        Project project = new Project();
        project.setId(1L);
        Mockito.when(projectService.findById(1L)).thenReturn(Optional.of(project));

        mockMvc.perform(post("/tasks/create")
                        .param("projectId", "1")
                        .flashAttr("task", new Task()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));
    }

    @Test
    @WithMockUser
    void testEditForm() throws Exception {
        Project project = new Project();
        project.setId(1L);

        Task task = new Task();
        task.setId(1L);
        task.setProject(project);

        Mockito.when(taskService.findById(1L)).thenReturn(Optional.of(task));
        Mockito.when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tasks/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/edit"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser
    void testEditTask() throws Exception {
        Task task = new Task();
        task.setId(1L);
        Project project = new Project();
        project.setId(1L);
        task.setProject(project);

        Mockito.when(projectService.findById(1L)).thenReturn(Optional.of(project));
        Mockito.when(userService.findAllById(List.of(1L))).thenReturn(List.of(new User()));

        mockMvc.perform(post("/tasks/edit")
                        .param("projectId", "1")
                        .param("assignees", "1")
                        .flashAttr("task", task))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));
    }

    @Test
    @WithMockUser
    void testListTasks() throws Exception {
        Mockito.when(taskService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/list"))
                .andExpect(model().attributeExists("tasks"));
    }

    @Test
    @WithMockUser
    void testAssignForm() throws Exception {
        Task task = new Task();
        task.setId(1L);
        Project project = new Project();
        project.setId(1L);
        task.setProject(project);

        Mockito.when(taskService.findById(1L)).thenReturn(Optional.of(task));
        Mockito.when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tasks/assign/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/assign"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser
    void testAssignUsers() throws Exception {
        Task task = new Task();
        Project project = new Project();
        project.setId(1L);
        task.setProject(project);

        Mockito.when(taskService.findById(1L)).thenReturn(Optional.of(task));
        Mockito.when(userService.findAllById(List.of(1L))).thenReturn(List.of(new User()));

        mockMvc.perform(post("/tasks/assign")
                        .param("taskId", "1")
                        .param("userIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testMyTasks() throws Exception {
        Mockito.when(taskService.findByAssigneeUsername("testuser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tasks/my").principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/my-tasks"))
                .andExpect(model().attributeExists("tasks"));
    }
}