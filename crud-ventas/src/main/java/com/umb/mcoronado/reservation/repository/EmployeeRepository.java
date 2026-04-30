package com.umb.mcoronado.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umb.mcoronado.reservation.model.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
