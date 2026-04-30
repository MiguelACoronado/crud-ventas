package com.umb.mcoronado.reservation.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umb.mcoronado.reservation.model.entity.EmployeeAudit;
import com.umb.mcoronado.reservation.repository.EmployeeAuditRepository;

@Service
public class EmployeeAuditService {

    private final EmployeeAuditRepository employeeAuditRepository;

    public EmployeeAuditService(EmployeeAuditRepository employeeAuditRepository) {
        this.employeeAuditRepository = employeeAuditRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeAudit> findAll() {
        return employeeAuditRepository.findAll(Sort.by(Sort.Direction.DESC, "changedAt"));
    }
}
