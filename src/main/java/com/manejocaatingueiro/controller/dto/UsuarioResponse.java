package com.manejocaatingueiro.controller.dto;

import com.manejocaatingueiro.model.FonteAgua;
import com.manejocaatingueiro.model.PerfilProdutivo;
import com.manejocaatingueiro.model.PerfilUsuario;
import com.manejocaatingueiro.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        PerfilUsuario perfil,
        FonteAgua abastecimento,
        Integer localidadeId,
        PerfilProdutivo perfilProdutivo,
        boolean emailVerificado,
        boolean consentimentoLocalizacao
) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getPerfil(),
                usuario.getAbastecimento(),
                usuario.getLocalidadeId(),
                usuario.getPerfilProdutivo(),
                usuario.isEmailVerificado(),
                usuario.isConsentimentoLocalizacao()
        );
    }
}
