package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.RegistroSafra;
import com.manejocaatingueiro.repository.RegistroSafraRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcRegistroSafraRepository implements RegistroSafraRepository {
    private static final String COLECAO = "historico_safras";
    private final JdbcDocumentStore store;

    public JdbcRegistroSafraRepository(JdbcDocumentStore store) {
        this.store = store;
    }

    @Override
    public List<RegistroSafra> listarPorUsuario(Long usuarioId) {
        return store.listar(COLECAO, RegistroSafra.class).stream()
                .filter(registro -> usuarioId.equals(registro.getUsuarioId()))
                .sorted(Comparator.comparing(RegistroSafra::getDataPlantio).reversed())
                .toList();
    }

    @Override
    public Optional<RegistroSafra> buscarPorId(Long id) {
        return store.buscar(COLECAO, id.toString(), RegistroSafra.class);
    }

    @Override
    public RegistroSafra salvar(RegistroSafra registro) {
        if (registro.getId() == null) {
            registro.setId(store.proximoId(COLECAO));
        }
        return store.salvar(COLECAO, registro.getId().toString(),
                registro.getLocalidadeId(), registro);
    }
}
