package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.AlertaClimatico;
import com.manejocaatingueiro.repository.AlertaRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
public class JdbcAlertaRepository implements AlertaRepository {
    private static final String COLECAO = "alertas_climaticos";
    private final JdbcDocumentStore store;

    public JdbcAlertaRepository(JdbcDocumentStore store) {
        this.store = store;
    }

    @Override
    public List<AlertaClimatico> listarPorLocalidade(Integer localidadeId) {
        return store.listarPorLocalidade(COLECAO, localidadeId, AlertaClimatico.class).stream()
                .sorted(Comparator.comparing(AlertaClimatico::getCriadoEm).reversed())
                .toList();
    }

    @Override
    public AlertaClimatico salvar(AlertaClimatico alerta) {
        if (alerta.getId() == null) {
            alerta.setId(store.proximoId(COLECAO));
        }
        return store.salvar(COLECAO, alerta.getId().toString(), alerta.getLocalidadeId(), alerta);
    }
}
