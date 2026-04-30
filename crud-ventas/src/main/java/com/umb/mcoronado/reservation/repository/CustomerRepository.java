package com.umb.mcoronado.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umb.mcoronado.reservation.model.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);
}
