package com.example.projectmanager.controller;

import com.example.projectmanager.model.Comment;
import com.example.projectmanager.model.Task;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.CommentRepository;
import com.example.projectmanager.repository.TaskRepository;
import com.example.projectmanager.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentRepository commentRepo;
    private final TaskRepository taskRepo;
    private final UserService userService;

    public CommentController(CommentRepository commentRepo, TaskRepository taskRepo, UserService userService) {
        this.commentRepo = commentRepo;
        this.taskRepo = taskRepo;
        this.userService = userService;
    }

    @PostMapping("/add")
    public String addComment(@RequestParam Long taskId,
                             @RequestParam String content,
                             @AuthenticationPrincipal UserDetails userDetails) {

        Task task = taskRepo.findById(taskId).orElseThrow();

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setContent(content);
        comment.setAuthor(user);
        comment.setCreatedAt(LocalDateTime.now());

        commentRepo.save(comment);

        return "redirect:/tasks/edit/" + taskId;
    }

    @PostMapping("/edit")
    public String editComment(@RequestParam Long commentId, @RequestParam String content) {
        Comment comment = commentRepo.findById(commentId).orElseThrow();
        comment.setContent(content);
        commentRepo.save(comment);
        return "redirect:/tasks/edit/" + comment.getTask().getId();
    }

    @PostMapping("/delete")
    public String deleteComment(@RequestParam Long commentId) {
        Comment comment = commentRepo.findById(commentId).orElseThrow();
        Long taskId = comment.getTask().getId();
        commentRepo.delete(comment);
        return "redirect:/tasks/edit/" + taskId;
    }
}