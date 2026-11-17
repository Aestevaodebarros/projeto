package com.manejocaatingueiro.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record PrecipitacaoRequest(
        @NotNull @DecimalMin("0.0") Double volumeMm,
        @NotBlank String fonte,
        @NotNull @PastOrPresent LocalDate data
) {
}
