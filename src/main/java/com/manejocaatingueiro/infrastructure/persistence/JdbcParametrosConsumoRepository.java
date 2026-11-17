package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.ParametrosConsumo;
import com.manejocaatingueiro.repository.ParametrosConsumoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JdbcParametrosConsumoRepository implements ParametrosConsumoRepository {
    private static final String COLECAO = "parametros_consumo";
    private static final String ID = "padrao";
    private final JdbcDocumentStore store;

    public JdbcParametrosConsumoRepository(JdbcDocumentStore store) {
        this.store = store;
    }

    @PostConstruct
    void inicializarPadroes() {
        if (buscar().isEmpty()) {
            salvar(new ParametrosConsumo());
        }
    }

    @Override
    public Optional<ParametrosConsumo> buscar() {
        return store.buscar(COLECAO, ID, ParametrosConsumo.class);
    }

    @Override
    public ParametrosConsumo salvar(ParametrosConsumo parametros) {
        return store.salvar(COLECAO, ID, null, parametros);
    }
}
