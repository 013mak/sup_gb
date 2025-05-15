package com.example.projectmanager.controller;

import com.example.projectmanager.filter.JwtAuthenticationFilter;
import com.example.projectmanager.model.Comment;
import com.example.projectmanager.model.Task;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.CommentRepository;
import com.example.projectmanager.repository.TaskRepository;
import com.example.projectmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentRepository commentRepo;

    @MockBean
    private TaskRepository taskRepo;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "testuser")
    void addComment_ShouldSaveAndRedirect() throws Exception {
        Task task = new Task();
        task.setId(1L);
        User user = new User();
        user.setId(10L);
        user.setUsername("testuser");

        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/comments/add")
                        .param("taskId", "1")
                        .param("content", "Some comment"))
                .andExpect(redirectedUrl("/tasks/edit/1"));

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepo).save(captor.capture());

        Comment saved = captor.getValue();
        assertThat(saved.getTask()).isEqualTo(task);
        assertThat(saved.getAuthor()).isEqualTo(user);
        assertThat(saved.getContent()).isEqualTo("Some comment");
        assertThat(saved.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void editComment_ShouldUpdateAndRedirect() throws Exception {
        Task task = new Task();
        task.setId(2L);
        Comment comment = new Comment();
        comment.setId(100L);
        comment.setTask(task);

        when(commentRepo.findById(100L)).thenReturn(Optional.of(comment));

        mockMvc.perform(post("/comments/edit")
                        .param("commentId", "100")
                        .param("content", "Updated"))
                .andExpect(redirectedUrl("/tasks/edit/2"));

        assertThat(comment.getContent()).isEqualTo("Updated");
        verify(commentRepo).save(comment);
    }

    @Test
    void deleteComment_ShouldRemoveAndRedirect() throws Exception {
        Task task = new Task();
        task.setId(3L);
        Comment comment = new Comment();
        comment.setId(200L);
        comment.setTask(task);

        when(commentRepo.findById(200L)).thenReturn(Optional.of(comment));

        mockMvc.perform(post("/comments/delete")
                        .param("commentId", "200"))
                .andExpect(redirectedUrl("/tasks/edit/3"));

        verify(commentRepo).delete(comment);
    }
}