package com.manejocaatingueiro.repository;

import com.manejocaatingueiro.model.Sincronizacao;

import java.util.Optional;

public interface SincronizacaoRepository {
    Optional<Sincronizacao> buscarPorUsuario(Long usuarioId);
    Sincronizacao salvar(Sincronizacao sincronizacao);
}
