package com.manejocaatingueiro.controller.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record NivelReservatorioRequest(
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double nivelPercentual
) {
}
