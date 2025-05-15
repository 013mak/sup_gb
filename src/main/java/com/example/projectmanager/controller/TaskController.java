
package com.example.projectmanager.controller;

import com.example.projectmanager.model.*;
import com.example.projectmanager.service.ProjectService;
import com.example.projectmanager.service.TaskService;
import com.example.projectmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;


    /*
    создание задачи. создается только в проекте, т.к. привязана к проекту.
     */
    @GetMapping("/create/{projectId}")
    public String createForm(@PathVariable Long projectId, Model model) {
        Task task = new Task();
        task.setProject(projectService.findById(projectId).orElse(null));
        model.addAttribute("task", task);
        model.addAttribute("projectId", projectId);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("statuses", Status.values());
        return "tasks/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Task task, @RequestParam Long projectId) {
        Project project = projectService.findById(projectId).orElse(null);
        if (project != null) {
            task.setProject(project);
            taskService.save(task);
        }
        return "redirect:/projects/" + projectId;
    }

    /*
    редактирование задачи
     */

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Task> taskOpt = taskService.findById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            model.addAttribute("task", task);
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("statuses", Status.values());
            model.addAttribute("users", userService.findAll());
            return "tasks/edit";
        } else {
            return "redirect:/projects";
        }
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute Task task,
                       @RequestParam Long projectId,
                       @RequestParam(name = "assignees", required = false) List<Long> userIds) {

        Project project = projectService.findById(projectId).orElse(null);
        if (project != null) {
            task.setProject(project);
            if (userIds != null) {
                Set<User> users = new HashSet<>(userService.findAllById(userIds));
                task.setAssignees(users);
            }
            taskService.save(task);
        }
        return "redirect:/projects/" + projectId;
    }

    /*
    просмотр списка задач
     */

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks/list";
    }

    /*
    назначение исполнителя на задачу
    */

    @GetMapping("/assign/{taskId}")
    public String assignForm(@PathVariable Long taskId, Model model) {
        Task task = taskService.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));
        model.addAttribute("task", task);
        model.addAttribute("users", userService.findAll());
        return "tasks/assign";
    }

    @PostMapping("/assign")
    public String assignUsers(@RequestParam Long taskId, @RequestParam List<Long> userIds) {
        Task task = taskService.findById(taskId).orElseThrow();
        Set<User> users = new HashSet<>(userService.findAllById(userIds));
        task.setAssignees(users);
        taskService.save(task);
        return "redirect:/projects/" + task.getProject().getId();
    }

    /*
    просмотр списка задач, закрепленных за исполнителем
     */
    @GetMapping("/my")
    public String myTasks(Model model, Principal principal) {
        String username = principal.getName();
        List<Task> myTasks = taskService.findByAssigneeUsername(username);
        model.addAttribute("tasks", myTasks);
        return "tasks/my-tasks";
    }
}
