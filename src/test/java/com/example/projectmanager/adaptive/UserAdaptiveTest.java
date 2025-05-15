package com.example.projectmanager.adaptive;

import com.example.projectmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class UserAdaptiveTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void user_CanViewOwnTasks() throws Exception {
        when(taskService.findByAssigneeUsername("user")).thenReturn(List.of());

        mockMvc.perform(get("/tasks/my"))
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/my-tasks"))
                .andExpect(model().attributeExists("tasks"));
    }
}
