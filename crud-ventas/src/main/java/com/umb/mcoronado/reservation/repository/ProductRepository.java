package com.umb.mcoronado.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umb.mcoronado.reservation.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
