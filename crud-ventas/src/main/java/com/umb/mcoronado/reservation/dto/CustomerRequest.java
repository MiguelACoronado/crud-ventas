package com.umb.mcoronado.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest {

    @NotBlank
    @Size(max = 120)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 150)
    private String email;
}
