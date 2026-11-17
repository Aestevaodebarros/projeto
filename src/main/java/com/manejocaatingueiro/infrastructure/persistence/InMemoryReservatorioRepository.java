package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.repository.ReservatorioRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryReservatorioRepository implements ReservatorioRepository {
    private final ConcurrentHashMap<Long, Reservatorio> reservatorios = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    public InMemoryReservatorioRepository() {
        Reservatorio cisterna = new Reservatorio();
        cisterna.setId(sequencia.getAndIncrement());
        cisterna.setNome("Cisterna comunitária 1");
        cisterna.setTipo("CISTERNA");
        cisterna.setLocalidadeId(1);
        cisterna.setCapacidadeLitros(15000.0);
        cisterna.setNivelPercentual(68.0);
        cisterna.setUltimaSincronizacao(Instant.now());
        reservatorios.put(cisterna.getId(), cisterna);

        Reservatorio poco = new Reservatorio();
        poco.setId(sequencia.getAndIncrement());
        poco.setNome("Poço da propriedade");
        poco.setTipo("POCO");
        poco.setLocalidadeId(1);
        poco.setCapacidadeLitros(30000.0);
        poco.setNivelPercentual(44.0);
        poco.setProfundidadeTotalM(20.0);
        poco.setProfundidadeLençolM(7.0);
        poco.setUltimaSincronizacao(Instant.now());
        reservatorios.put(poco.getId(), poco);
    }

    @Override
    public List<Reservatorio> listarPorLocalidade(Integer localidadeId) {
        return reservatorios.values().stream()
                .filter(reservatorio -> localidadeId.equals(reservatorio.getLocalidadeId()))
                .sorted(Comparator.comparing(Reservatorio::getId))
                .toList();
    }

    @Override
    public Optional<Reservatorio> buscarPorId(Long id) {
        return Optional.ofNullable(reservatorios.get(id));
    }

    @Override
    public Reservatorio salvar(Reservatorio reservatorio) {
        if (reservatorio.getId() == null) {
            reservatorio.setId(sequencia.getAndIncrement());
        } else {
            sequencia.accumulateAndGet(reservatorio.getId() + 1, Math::max);
        }
        reservatorios.put(reservatorio.getId(), reservatorio);
        return reservatorio;
    }
}
