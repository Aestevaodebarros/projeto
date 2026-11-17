package com.manejocaatingueiro.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LimiarRequest(
        @NotNull @Positive Long usuarioId,
        @NotNull @Positive Double limiarMm
) {
}
