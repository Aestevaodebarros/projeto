package com.manejocaatingueiro.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record AlertaRequest(
        @NotBlank String tipo,
        @NotBlank String severidade,
        @NotBlank String mensagem,
        String fonte
) {
}
