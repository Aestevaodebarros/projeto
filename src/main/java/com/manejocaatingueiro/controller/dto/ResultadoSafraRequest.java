package com.manejocaatingueiro.controller.dto;

import com.manejocaatingueiro.model.StatusSafra;
import jakarta.validation.constraints.NotNull;

public record ResultadoSafraRequest(@NotNull StatusSafra resultado, String observacao) {
}
