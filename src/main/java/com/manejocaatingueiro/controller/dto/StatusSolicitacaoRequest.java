package com.manejocaatingueiro.controller.dto;

import com.manejocaatingueiro.model.StatusSolicitacaoAgua;
import jakarta.validation.constraints.NotNull;

public record StatusSolicitacaoRequest(@NotNull StatusSolicitacaoAgua status) {
}
