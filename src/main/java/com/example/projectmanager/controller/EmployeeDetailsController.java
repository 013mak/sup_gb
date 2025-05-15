package com.example.projectmanager.controller;

import com.example.projectmanager.model.EmployeeDetails;
import com.example.projectmanager.model.User;
import com.example.projectmanager.repository.EmployeeDetailsRepository;
import com.example.projectmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/admin/employees")
@PreAuthorize("hasRole('ADMIN')")
public class EmployeeDetailsController {

    private final EmployeeDetailsRepository employeeDetailsRepository;
    private final UserRepository userRepository;

    public EmployeeDetailsController(EmployeeDetailsRepository edRepo, UserRepository uRepo) {
        this.employeeDetailsRepository = edRepo;
        this.userRepository = uRepo;
    }

    /*
    заполнение профиля сотрудника. просматривается только через профиль пользователя админом
    (возможно стоит отдать менеджерам, а админу оставить только права)
     */
    @GetMapping("/edit/{userId}")
    public String edit(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        EmployeeDetails details = user.getEmployeeDetails();
        if (details == null) {
            details = new EmployeeDetails();
            details.setUser(user);
        }
        model.addAttribute("details", details);
        return "admin/employee-edit";
    }

    @PostMapping("/edit")
    public String save(@ModelAttribute EmployeeDetails details) {
        if (details.getUser() == null || details.getUser().getId() == null) {
            throw new IllegalArgumentException("User ID is missing");
        }

        User user = userRepository.findById(details.getUser().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        details.setUser(user);

        employeeDetailsRepository.save(details);
        return "redirect:/admin/users";
    }

    /*
    просмотр профиля сотрудника
     */
    @GetMapping("/{userId}")
    public String view(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        EmployeeDetails details = user.getEmployeeDetails();
        if (details == null) {
            details = new EmployeeDetails();
            details.setUser(user);
        }
        model.addAttribute("details", details);
        return "admin/employee-view";
    }

//    /*
//    просмотр профилей всех сотрудников
//    */
//    @GetMapping
//    public String listAll(Model model) {
//        List<EmployeeDetails> all = employeeDetailsRepository.findAll();
//        model.addAttribute("employees", all);
//        return "admin/employee-list";
//    }


}
