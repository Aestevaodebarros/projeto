package com.manejocaatingueiro.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ChecklistRequest(
        @NotBlank String item,
        boolean conforme
) {
}
