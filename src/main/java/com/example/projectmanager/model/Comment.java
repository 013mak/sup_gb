package com.example.projectmanager.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    private Task task;

    @Column(nullable = false)
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();


}