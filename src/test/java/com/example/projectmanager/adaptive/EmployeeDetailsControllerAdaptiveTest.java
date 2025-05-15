package com.example.projectmanager.adaptive;

import com.example.projectmanager.model.EmployeeDetails;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.EmployeeDetailsRepository;
import com.example.projectmanager.repository.UserRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.stream.Stream;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeDetailsControllerAdaptiveTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeDetailsRepository employeeDetailsRepository;

    @MockBean
    private UserRepository userRepository;

    static Stream<User> users() {
        User userWithDetails = new User();
        userWithDetails.setId(1L);
        EmployeeDetails details = new EmployeeDetails();
        details.setUser(userWithDetails);
        userWithDetails.setEmployeeDetails(details);

        User userWithoutDetails = new User();
        userWithoutDetails.setId(2L);
        userWithoutDetails.setEmployeeDetails(null);

        return Stream.of(userWithDetails, userWithoutDetails);
    }

    @ParameterizedTest
    @MethodSource("users")
    @WithMockUser(roles = "ADMIN")
    void editViewLoadsForUserWithOrWithoutDetails(User user) throws Exception {
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/admin/employees/edit/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/employee-edit"))
                .andExpect(model().attributeExists("details"));
    }

    @ParameterizedTest
    @MethodSource("users")
    @WithMockUser(roles = "ADMIN")
    void saveEmployeeDetailsWorks(User user) throws Exception {
        EmployeeDetails details = new EmployeeDetails();
        details.setUser(user);

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/admin/employees/edit")
                        .param("user.id", String.valueOf(user.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));
    }
}
