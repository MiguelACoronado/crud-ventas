package com.umb.mcoronado.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umb.mcoronado.reservation.dto.ProductRequest;
import com.umb.mcoronado.reservation.exception.ProductNotFoundException;
import com.umb.mcoronado.reservation.model.entity.Product;
import com.umb.mcoronado.reservation.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public Product create(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductRequest request) {
        Product product = findById(id);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }
}
