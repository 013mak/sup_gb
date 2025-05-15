package com.example.projectmanager.adaptive;
import com.example.projectmanager.model.Priority;
import com.example.projectmanager.model.Project;
import com.example.projectmanager.model.Status;
import com.example.projectmanager.model.Task;
import com.example.projectmanager.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    @SpringBootTest
    @AutoConfigureMockMvc
    public class TaskControllerAdaptiveTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TaskService taskService;

        @Test
        @WithMockUser(username = "testuser")
        void testViewMyTasksWithData() throws Exception {
            Project project = new Project();
            project.setId(1L);
            project.setName("Test Project");


            Task task = new Task();
            task.setTitle("Test Task");
            task.setDescription("Test Description");
            task.setProject(project);
            task.setPriority(Priority.HIGH);
            task.setDeadline(LocalDate.now().plusDays(7));
            task.setStatus(Status.IN_PROGRESS);


            Mockito.when(taskService.findByAssigneeUsername("testuser"))
                    .thenReturn(Collections.singletonList(task));


            mockMvc.perform(get("/tasks/my"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("tasks/my-tasks"))
                    .andExpect(model().attributeExists("tasks"));
        }


        @Test
        @WithMockUser(username = "testuser")
        void testViewMyTasksEmpty() throws Exception {
            Mockito.when(taskService.findByAssigneeUsername("testuser"))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/tasks/my"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("tasks/my-tasks"))
                    .andExpect(model().attributeExists("tasks"));
        }

        @Test
        @WithMockUser(roles = "MANAGER")
        void testAccessToTasksAsManager() throws Exception {
            mockMvc.perform(get("/tasks/my"))
                    .andExpect(status().isOk());
        }
    }
