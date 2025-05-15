package com.example.projectmanager.repository;

import com.example.projectmanager.model.EmployeeDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDetailsRepository extends JpaRepository<EmployeeDetails, Long> {
}
