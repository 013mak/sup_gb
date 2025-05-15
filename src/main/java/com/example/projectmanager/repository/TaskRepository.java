package com.example.projectmanager.repository;

import com.example.projectmanager.model.Project;
import com.example.projectmanager.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @EntityGraph(attributePaths = {"assignees"})
    List<Task> findByProject(Project project);

    List<Task> findByAssignees_Username(String username);
}