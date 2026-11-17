package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.AvisoComunitario;
import com.manejocaatingueiro.repository.AvisoComunitarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
public class JdbcAvisoComunitarioRepository implements AvisoComunitarioRepository {
    private static final String COLECAO = "avisos_comunitarios";
    private final JdbcDocumentStore store;

    public JdbcAvisoComunitarioRepository(JdbcDocumentStore store) {
        this.store = store;
    }

    @PostConstruct
    void inicializarAvisosDemonstrativos() {
        if (store.listar(COLECAO, AvisoComunitario.class).isEmpty()) {
            AvisoComunitario aviso = new AvisoComunitario(
                    store.proximoId(COLECAO),
                    1,
                    "Mutirão de limpeza",
                    "Amanhã, a partir das 8h, mutirão para limpar a área da cisterna comunitária.",
                    "Líder comunitário"
            );
            salvar(aviso);
        }
    }

    @Override
    public List<AvisoComunitario> listarPorLocalidade(Integer localidadeId) {
        List<AvisoComunitario> avisos = localidadeId == null
                ? store.listar(COLECAO, AvisoComunitario.class)
                : store.listarPorLocalidade(COLECAO, localidadeId, AvisoComunitario.class);
        return avisos.stream()
                .sorted(Comparator.comparing(AvisoComunitario::getCriadoEm).reversed())
                .toList();
    }

    @Override
    public AvisoComunitario salvar(AvisoComunitario aviso) {
        if (aviso.getId() == null) {
            aviso.setId(store.proximoId(COLECAO));
        }
        return store.salvar(COLECAO, aviso.getId().toString(), aviso.getLocalidadeId(), aviso);
    }
}
