package com.manejocaatingueiro.controller.dto;

public record NivelReservatorioRequest(
        Double nivelPercentual,
        String nivelQualitativo,
        Double profundidadeLencolM
) {
}
