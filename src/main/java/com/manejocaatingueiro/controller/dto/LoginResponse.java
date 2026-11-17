package com.manejocaatingueiro.controller.dto;

public record LoginResponse(
        String token,
        String tipoToken,
        UsuarioResponse usuario
) {
}
