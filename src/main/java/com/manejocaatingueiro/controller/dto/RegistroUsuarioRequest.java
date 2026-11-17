package com.manejocaatingueiro.controller.dto;

import com.manejocaatingueiro.model.FonteAgua;
import com.manejocaatingueiro.model.PerfilProdutivo;
import com.manejocaatingueiro.model.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistroUsuarioRequest(
        @NotBlank String nome,
        @Email @NotBlank String email,
        @NotBlank String telefone,
        @NotBlank String senha,
        PerfilUsuario perfil,
        FonteAgua abastecimento,
        Integer localidadeId,
        PerfilProdutivo perfilProdutivo,
        Boolean consentimentoLocalizacao
) {
}
