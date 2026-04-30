package com.umb.mcoronado.reservation.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterSaleRequest {

    @NotNull
    private Long customerId;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal subtotal;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal ivaPercentage;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal discountPercentage;
}
