package com.example.projectmanager.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Data
@Entity
public class EmployeeDetails {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private String lastName;
    private String firstName;
    private LocalDate birthDate;
    private String phone;
    private String email;


}
