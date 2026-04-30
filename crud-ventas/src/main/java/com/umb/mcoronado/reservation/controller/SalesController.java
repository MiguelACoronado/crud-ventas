package com.umb.mcoronado.reservation.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umb.mcoronado.reservation.dto.RegisterSaleRequest;
import com.umb.mcoronado.reservation.service.SalesDatabaseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesDatabaseService salesDatabaseService;

    public SalesController(SalesDatabaseService salesDatabaseService) {
        this.salesDatabaseService = salesDatabaseService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerSale(@Valid @RequestBody RegisterSaleRequest request) {
        Long saleId = salesDatabaseService.registerSale(
                request.getCustomerId(),
                request.getSubtotal(),
                request.getIvaPercentage(),
                request.getDiscountPercentage());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("saleId", saleId));
    }

    @GetMapping("/total-by-customer/{customerId}")
    public ResponseEntity<Map<String, Object>> totalByCustomer(@PathVariable Long customerId) {
        BigDecimal total = salesDatabaseService.totalSoldByCustomer(customerId);
        return ResponseEntity.ok(Map.of("customerId", customerId, "total", total));
    }

    @GetMapping("/invoice-total")
    public ResponseEntity<Map<String, Object>> invoiceTotal(
            @RequestParam BigDecimal subtotal,
            @RequestParam(defaultValue = "19")  ivaPercentage,
            @RequestParam(defaultValue = "0")  discountPercentage) {
        BigDecimal total = salesDatabaseService.calculateInvoiceTotal(subtotal, ivaPercentage, discountPercentage);
        return ResponseEntity.ok(Map.of(
                "subtotal", subtotal,
                "ivaPercentage", ivaPercentage,
                "discountPercentage", discountPercentage,
                "total", total));
    }

    @GetMapping("/invoice-total-with-tax")
    public ResponseEntity<Map<String, Object>> invoiceTotalWithTax(
            @RequestParam BigDecimal subtotal,
            @RequestParam(defaultValue = "19") ivaPercentage) {
        BigDecimal total = salesDatabaseService.calculateInvoiceTotalWithTax(subtotal, ivaPercentage);
        return ResponseEntity.ok(Map.of(
                "subtotal", subtotal,
                "ivaPercentage", ivaPercentage,
                "total", total));
    }
}
