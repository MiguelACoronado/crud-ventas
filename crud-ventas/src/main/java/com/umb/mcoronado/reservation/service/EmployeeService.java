package com.umb.mcoronado.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umb.mcoronado.reservation.dto.EmployeeRequest;
import com.umb.mcoronado.reservation.exception.EmployeeNotFoundException;
import com.umb.mcoronado.reservation.model.entity.Employee;
import com.umb.mcoronado.reservation.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Transactional
    public Employee create(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setFullName(request.getFullName());
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee update(Long id, EmployeeRequest request) {
        Employee employee = findById(id);
        employee.setFullName(request.getFullName());
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());
        return employeeRepository.save(employee);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = findById(id);
        employeeRepository.delete(employee);
    }
}
