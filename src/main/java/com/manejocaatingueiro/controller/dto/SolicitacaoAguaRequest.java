package com.manejocaatingueiro.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SolicitacaoAguaRequest(
        @NotNull @Positive Long reservatorioId,
        @NotBlank String motivo
) {
}
