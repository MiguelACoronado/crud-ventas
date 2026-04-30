package com.umb.mcoronado.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umb.mcoronado.reservation.dto.CustomerRequest;
import com.umb.mcoronado.reservation.exception.BusinessRuleViolationException;
import com.umb.mcoronado.reservation.model.entity.Customer;
import com.umb.mcoronado.reservation.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer create(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleViolationException("Ya existe un cliente con ese correo.");
        }
        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        return customerRepository.save(customer);
    }
}
