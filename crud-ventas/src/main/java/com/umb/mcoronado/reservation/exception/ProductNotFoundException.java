package com.umb.mcoronado.reservation.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product not found for id: " + id);
    }
}
