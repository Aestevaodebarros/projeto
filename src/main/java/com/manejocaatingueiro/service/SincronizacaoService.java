package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.PendenciaSincronizacao;
import com.manejocaatingueiro.model.Sincronizacao;
import com.manejocaatingueiro.repository.SincronizacaoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class SincronizacaoService {
    private final SincronizacaoRepository repository;

    public SincronizacaoService(SincronizacaoRepository repository) {
        this.repository = repository;
    }

    public Sincronizacao consultar(Long usuarioId) {
        if (usuarioId == null) throw new IllegalArgumentException("Usuário é obrigatório.");
        return repository.buscarPorUsuario(usuarioId).orElseGet(() -> {
            Sincronizacao nova = new Sincronizacao();
            nova.setIdSincronizacao(usuarioId);
            nova.setUsuarioId(usuarioId);
            return nova;
        });
    }

    public synchronized Sincronizacao registrarPendencia(Long usuarioId, String entidade, String operacao, String referenciaId) {
        if (usuarioId == null || entidade == null || entidade.isBlank()
                || operacao == null || operacao.isBlank()) {
            throw new IllegalArgumentException("Usuário, entidade e operação são obrigatórios para a fila de sincronização.");
        }
        Sincronizacao sincronizacao = consultar(usuarioId);
        PendenciaSincronizacao pendencia = new PendenciaSincronizacao();
        pendencia.setId(UUID.randomUUID().toString());
        pendencia.setEntidade(entidade);
        pendencia.setOperacao(operacao);
        pendencia.setReferenciaId(referenciaId);
        pendencia.setCriadaEm(Instant.now());
        sincronizacao.getPendencias().add(pendencia);
        sincronizacao.setStatus("PENDENTE_SINCRONIZACAO_EXTERNA");
        sincronizacao.setAtualizadoEm(Instant.now());
        return repository.salvar(sincronizacao);
    }
}
