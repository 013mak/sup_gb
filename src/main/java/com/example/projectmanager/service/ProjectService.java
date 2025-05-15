package com.example.projectmanager.service;

import com.example.projectmanager.model.Project;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.ProjectRepository;
import com.example.projectmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public Project save(Project project) {
        return projectRepository.save(project);
    }


    public List<User> findAllUsers() {
        return userRepository.findAll();
    }


    public void assignUsers(Long projectId, List<Long> userIds) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        List<User> usersToAssign = userRepository.findAllById(userIds);

        for (User user : usersToAssign) {
            if (!project.getUsers().contains(user)) {
                project.getUsers().add(user);
            }
        }

        projectRepository.save(project);
    }


    public List<Project> findByUserUsername(String username) {
        return projectRepository.findByUsers_Username(username);
    }
}