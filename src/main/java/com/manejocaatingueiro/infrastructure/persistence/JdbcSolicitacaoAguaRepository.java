package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.SolicitacaoAgua;
import com.manejocaatingueiro.repository.SolicitacaoAguaRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcSolicitacaoAguaRepository implements SolicitacaoAguaRepository {
    private static final String COLECAO = "solicitacoes_agua";
    private final JdbcDocumentStore store;

    public JdbcSolicitacaoAguaRepository(JdbcDocumentStore store) {
        this.store = store;
    }

    @Override
    public List<SolicitacaoAgua> listarPorLocalidade(Integer localidadeId) {
        return store.listarPorLocalidade(COLECAO, localidadeId, SolicitacaoAgua.class).stream()
                .sorted(Comparator.comparing(SolicitacaoAgua::getCriadaEm).reversed())
                .toList();
    }

    @Override
    public List<SolicitacaoAgua> listarPorUsuario(Long usuarioId) {
        return store.listar(COLECAO, SolicitacaoAgua.class).stream()
                .filter(solicitacao -> usuarioId.equals(solicitacao.getUsuarioId()))
                .sorted(Comparator.comparing(SolicitacaoAgua::getCriadaEm).reversed())
                .toList();
    }

    @Override
    public Optional<SolicitacaoAgua> buscarPorId(Long id) {
        return store.buscar(COLECAO, id.toString(), SolicitacaoAgua.class);
    }

    @Override
    public SolicitacaoAgua salvar(SolicitacaoAgua solicitacao) {
        if (solicitacao.getId() == null) {
            solicitacao.setId(store.proximoId(COLECAO));
        }
        return store.salvar(COLECAO, solicitacao.getId().toString(),
                solicitacao.getLocalidadeId(), solicitacao);
    }
}
