package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.Sincronizacao;
import com.manejocaatingueiro.repository.SincronizacaoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JdbcSincronizacaoRepository implements SincronizacaoRepository {
    private static final String COLECAO = "sincronizacoes";
    private final JdbcDocumentStore store;

    public JdbcSincronizacaoRepository(JdbcDocumentStore store) {
        this.store = store;
    }

    @Override
    public Optional<Sincronizacao> buscarPorUsuario(Long usuarioId) {
        if (usuarioId == null) return Optional.empty();
        return store.buscar(COLECAO, usuarioId.toString(), Sincronizacao.class);
    }

    @Override
    public Sincronizacao salvar(Sincronizacao sincronizacao) {
        if (sincronizacao.getUsuarioId() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório para registrar sincronização.");
        }
        sincronizacao.setIdSincronizacao(sincronizacao.getUsuarioId());
        return store.salvar(COLECAO, sincronizacao.getUsuarioId().toString(), null, sincronizacao);
    }
}
