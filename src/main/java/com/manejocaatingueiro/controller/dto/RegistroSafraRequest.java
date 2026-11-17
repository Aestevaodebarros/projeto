package com.manejocaatingueiro.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record RegistroSafraRequest(
        @NotNull @Positive Integer culturaId,
        @NotNull LocalDate dataPlantio,
        String observacao
) {
}
