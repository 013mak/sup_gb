package com.example.projectmanager.adaptive;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
class AccessControlTest {

    @Autowired private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void user_CannotAccessAdminPages() throws Exception {
        mockMvc.perform(get("/admin/employees/edit/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousUser_CannotAccessProtectedResources() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().is3xxRedirection()) // redirect to login
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }
}