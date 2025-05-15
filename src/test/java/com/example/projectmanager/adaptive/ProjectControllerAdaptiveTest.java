package com.example.projectmanager.adaptive;


import com.example.projectmanager.model.Project;
import com.example.projectmanager.model.User;
import com.example.projectmanager.service.ProjectService;
import com.example.projectmanager.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerAdaptiveTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testViewProject_AsAdmin() throws Exception {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        Mockito.when(projectService.findById(1L)).thenReturn(Optional.of(project));

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("projects/details"))
                .andExpect(model().attributeExists("project"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testViewNonExistentProject() throws Exception {
        Mockito.when(projectService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/projects/99"))
                .andExpect(redirectedUrl("/projects"));
    }
}
