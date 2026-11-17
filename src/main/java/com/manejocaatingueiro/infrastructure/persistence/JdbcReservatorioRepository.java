package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.repository.ReservatorioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcReservatorioRepository implements ReservatorioRepository {
    private static final String COLECAO = "reservatorios";
    private final JdbcDocumentStore store;

    public JdbcReservatorioRepository(JdbcDocumentStore store) {
        this.store = store;
    }

    @PostConstruct
    void inicializarDadosDemonstrativos() {
        if (store.listar(COLECAO, Reservatorio.class).isEmpty()) {
            Reservatorio cisterna = new Reservatorio();
            cisterna.setId(store.proximoId(COLECAO));
            cisterna.setUsuarioResponsavelId(1L);
            cisterna.setNome("Cisterna comunitária 1");
            cisterna.setTipo("CISTERNA");
            cisterna.setLocalidadeId(1);
            cisterna.setCapacidadeLitros(15000.0);
            cisterna.setNivelPercentual(68.0);
            cisterna.setUltimaSincronizacao(Instant.now());
            salvar(cisterna);

            Reservatorio poco = new Reservatorio();
            poco.setId(store.proximoId(COLECAO));
            poco.setUsuarioResponsavelId(1L);
            poco.setNome("Poço da propriedade");
            poco.setTipo("POCO");
            poco.setLocalidadeId(1);
            poco.setCapacidadeLitros(30000.0);
            poco.setNivelPercentual(44.0);
            poco.setProfundidadeTotalM(20.0);
            poco.setProfundidadeLençolM(7.0);
            poco.setUltimaSincronizacao(Instant.now());
            salvar(poco);
        }
    }

    @Override
    public List<Reservatorio> listarPorLocalidade(Integer localidadeId) {
        return store.listarPorLocalidade(COLECAO, localidadeId, Reservatorio.class).stream()
                .sorted(Comparator.comparing(Reservatorio::getId))
                .toList();
    }

    @Override
    public List<Reservatorio> listarPorUsuario(Long usuarioId) {
        return store.listar(COLECAO, Reservatorio.class).stream()
                .filter(reservatorio -> usuarioId.equals(reservatorio.getUsuarioResponsavelId()))
                .sorted(Comparator.comparing(Reservatorio::getId))
                .toList();
    }

    @Override
    public Optional<Reservatorio> buscarPorId(Long id) {
        return store.buscar(COLECAO, id.toString(), Reservatorio.class);
    }

    @Override
    public Reservatorio salvar(Reservatorio reservatorio) {
        if (reservatorio.getId() == null) {
            reservatorio.setId(store.proximoId(COLECAO));
        }
        reservatorio.setUltimaSincronizacao(Instant.now());
        return store.salvar(COLECAO, reservatorio.getId().toString(), reservatorio.getLocalidadeId(), reservatorio);
    }
}
