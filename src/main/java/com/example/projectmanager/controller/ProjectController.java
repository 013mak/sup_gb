package com.example.projectmanager.controller;

import com.example.projectmanager.model.Project;
import com.example.projectmanager.model.Priority;
import com.example.projectmanager.model.User;
import com.example.projectmanager.service.ProjectService;
import com.example.projectmanager.service.TaskService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    private final TaskService taskService;

    public ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    /*
    список проектов. она же базовая страница редиректа после входа.
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("projects", projectService.findAll());
        return "projects/list";
    }
    /*
    создание проекта. возможно для админа и менеджера
     */

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("priorities", Priority.values());
        return "projects/create";
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/create")
    public String create(@ModelAttribute Project project) {
        projectService.save(project);
        return "redirect:/projects";
    }

    /*
    просмотр проекта
     */

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Optional<Project> projectOpt = projectService.findById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            model.addAttribute("project", project);
            model.addAttribute("tasks", taskService.findByProjectWithAssignees(project));
            return "projects/details";
        } else {
            return "redirect:/projects";
        }
    }

    /*
    назначение ответственного за проект
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/{id}/assign")
    public String assignUserForm(@PathVariable Long id, Model model) {
        Project project = projectService.findById(id).orElseThrow();
        List<User> allUsers = projectService.findAllUsers();
        model.addAttribute("project", project);
        model.addAttribute("users", allUsers);
        return "projects/assign";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{id}/assign")
    public String assignUsers(@PathVariable Long id, @RequestParam List<Long> userIds) {
        projectService.assignUsers(id, userIds);

        return "redirect:/projects/" + id;
    }

    /*
    редактирование проекта
    (возможно дать права ролям админ и менеджер )
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Project> projectOpt = projectService.findById(id);
        if (projectOpt.isPresent()) {
            model.addAttribute("project", projectOpt.get());
            model.addAttribute("priorities", Priority.values());
            return "projects/edit";
        } else {
            return "redirect:/projects";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Project updatedProject) {
        Project existing = projectService.findById(id).orElseThrow();

              existing.setName(updatedProject.getName());
              existing.setDescription(updatedProject.getDescription());
              existing.setPriority(updatedProject.getPriority());

        projectService.save(existing);
        return "redirect:/projects";
    }

    /*
    просмотр проектов пользователя
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public String myProjects(Model model, Principal principal) {
        String username = principal.getName();
        List<Project> projects = projectService.findByUserUsername(username);
        model.addAttribute("projects", projects);
        return "projects/my-projects";
    }

}