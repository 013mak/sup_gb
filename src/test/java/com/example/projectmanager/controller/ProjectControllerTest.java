package com.example.projectmanager.controller;

import com.example.projectmanager.config.TestSecurityConfig;
import com.example.projectmanager.model.Project;
import com.example.projectmanager.model.User;
import com.example.projectmanager.service.ProjectService;
import com.example.projectmanager.service.TaskService;

import com.example.projectmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@Import(TestSecurityConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private TaskService taskService;

    @MockBean
    private UserService userService;



    @Test
    @WithMockUser(roles = "ADMIN")
    void testListProjects() throws Exception {
        Project project = new Project();
        project.setName("Test Project");

        when(projectService.findAll()).thenReturn(List.of(project));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/list"))
                .andExpect(model().attributeExists("projects"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProjectForm() throws Exception {
        mockMvc.perform(get("/projects/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/create"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attributeExists("priorities"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProjectSubmit() throws Exception {
        mockMvc.perform(post("/projects/create")
                        .param("name", "New Project")
                        .param("description", "Test description")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        verify(projectService, times(1)).save(any(Project.class));
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    void testDetailsFound() throws Exception {
        Project project = new Project();
        project.setId(1L);
        when(projectService.findById(1L)).thenReturn(Optional.of(project));
        when(taskService.findByProjectWithAssignees(project)).thenReturn(List.of());

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/details"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attributeExists("tasks"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDetailsNotFound() throws Exception {
        when(projectService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignUsersForm() throws Exception {
        Project project = new Project();
        when(projectService.findById(1L)).thenReturn(Optional.of(project));
        when(projectService.findAllUsers()).thenReturn(List.of(new User()));

        mockMvc.perform(get("/projects/1/assign"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/assign"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignUsersSubmit() throws Exception {
        mockMvc.perform(post("/projects/1/assign")
                        .param("userIds", "1", "2")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));

        verify(projectService).assignUsers(eq(1L), anyList());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testEditProjectForm() throws Exception {
        Project project = new Project();
        project.setId(1L);
        when(projectService.findById(1L)).thenReturn(Optional.of(project));

        mockMvc.perform(get("/projects/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/edit"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attributeExists("priorities"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void testEditProjectSubmit() throws Exception {
        Project existing = new Project();
        existing.setId(1L);
        when(projectService.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(post("/projects/edit/1")
                        .param("name", "Updated Name")
                        .param("description", "Updated Description")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        verify(projectService).save(any(Project.class));
    }

    @Test
    @WithMockUser(roles = "USER", username = "johndoe")
    void testMyProjects() throws Exception {
        when(projectService.findByUserUsername("johndoe")).thenReturn(List.of(new Project()));

        mockMvc.perform(get("/projects/my"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/my-projects"))
                .andExpect(model().attributeExists("projects"));
    }
}