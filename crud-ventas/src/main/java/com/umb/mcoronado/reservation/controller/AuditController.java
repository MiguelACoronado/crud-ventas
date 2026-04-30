package com.umb.mcoronado.reservation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umb.mcoronado.reservation.model.entity.EmployeeAudit;
import com.umb.mcoronado.reservation.service.EmployeeAuditService;

@RestController
@RequestMapping("/api/audits")
public class AuditController {

    private final EmployeeAuditService employeeAuditService;

    public AuditController(EmployeeAuditService employeeAuditService) {
        this.employeeAuditService = employeeAuditService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeAudit>> findAll() {
        return ResponseEntity.ok(employeeAuditService.findAll());
    }
}
