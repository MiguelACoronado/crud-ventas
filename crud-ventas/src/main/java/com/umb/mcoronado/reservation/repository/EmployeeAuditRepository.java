package com.umb.mcoronado.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umb.mcoronado.reservation.model.entity.EmployeeAudit;

public interface EmployeeAuditRepository extends JpaRepository<EmployeeAudit, Long> {
}
