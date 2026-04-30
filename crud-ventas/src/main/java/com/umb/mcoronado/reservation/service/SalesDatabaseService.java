package com.umb.mcoronado.reservation.service;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Types;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umb.mcoronado.reservation.exception.BusinessRuleViolationException;

@Service
public class SalesDatabaseService {

    private final JdbcTemplate jdbcTemplate;

    public SalesDatabaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Long registerSale(Long customerId, BigDecimal subtotal, BigDecimal ivaPercentage, BigDecimal discountPercentage) {
        validateCustomerExists(customerId);
        return jdbcTemplate.execute(connection -> {
            CallableStatement statement = connection.prepareCall("CALL sp_registrar_venta(?, ?, ?, ?, ?)");
            statement.setLong(1, customerId);
            statement.setBigDecimal(2, subtotal);
            statement.setBigDecimal(3, ivaPercentage);
            statement.setBigDecimal(4, discountPercentage);
            statement.registerOutParameter(5, Types.BIGINT);
            return statement;
        }, (CallableStatement statement) -> {
            statement.execute();
            return statement.getLong(5);
        });
    }

    private void validateCustomerExists(Long customerId) {
        Boolean customerExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM customers WHERE id = ?)",
                Boolean.class,
                customerId);
        if (Boolean.FALSE.equals(customerExists)) {
            throw new BusinessRuleViolationException("El cliente con id " + customerId + " no existe.");
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal totalSoldByCustomer(Long customerId) {
        return jdbcTemplate.execute(connection -> {
            CallableStatement statement = connection.prepareCall("CALL sp_total_vendido_por_cliente(?, ?)");
            statement.setLong(1, customerId);
            statement.registerOutParameter(2, Types.NUMERIC);
            return statement;
        }, (CallableStatement statement) -> {
            statement.execute();
            BigDecimal total = statement.getBigDecimal(2);
            return total == null ? BigDecimal.ZERO : total;
        });
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateInvoiceTotal(BigDecimal subtotal, BigDecimal ivaPercentage, BigDecimal discountPercentage) {
        BigDecimal total = jdbcTemplate.queryForObject(
                "SELECT fn_total_factura(?, ?, ?)",
                BigDecimal.class,
                subtotal,
                ivaPercentage,
                discountPercentage);
        return total == null ? BigDecimal.ZERO : total;
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateInvoiceTotalWithTax(BigDecimal subtotal, BigDecimal ivaPercentage) {
        BigDecimal total = jdbcTemplate.queryForObject(
                "SELECT fn_total_con_impuestos(?, ?)",
                BigDecimal.class,
                subtotal,
                ivaPercentage);
        return total == null ? BigDecimal.ZERO : total;
    }
}
