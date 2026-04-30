package com.umb.mcoronado.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReservationRequest {

    @NotBlank
    @Size(max = 100)
    private String customerName;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime time;

    @NotBlank
    @Size(max = 50)
    private String service;
}
