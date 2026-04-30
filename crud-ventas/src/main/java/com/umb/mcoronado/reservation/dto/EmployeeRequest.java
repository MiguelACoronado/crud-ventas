package com.umb.mcoronado.reservation.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRequest {

    @NotBlank
    @Size(max = 120)
    private String fullName;

    @NotBlank
    @Size(max = 80)
    private String position;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal salary;
}
