package com.manejocaatingueiro.repository;

import com.manejocaatingueiro.model.SolicitacaoAgua;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoAguaRepository {
    List<SolicitacaoAgua> listarPorLocalidade(Integer localidadeId);
    List<SolicitacaoAgua> listarPorUsuario(Long usuarioId);
    Optional<SolicitacaoAgua> buscarPorId(Long id);
    SolicitacaoAgua salvar(SolicitacaoAgua solicitacao);
}
