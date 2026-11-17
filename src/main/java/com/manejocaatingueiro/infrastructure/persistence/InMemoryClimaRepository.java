package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.Localidade;
import com.manejocaatingueiro.model.Precipitacao;
import com.manejocaatingueiro.repository.ClimaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryClimaRepository implements ClimaRepository {
    private final List<Localidade> localidades = List.of(
            new Localidade(1, "Irecê", "MUNICIPIO", null, "2914604", "BA", -11.30, -41.85, "A424"),
            new Localidade(2, "Presidente Dutra", "MUNICIPIO", null, "2925709", "BA", -11.29, -41.98, "A424"),
            new Localidade(3, "Povoado de Achado (Irecê)", "POVOADO", 1, "2914604", "BA", -11.26, -41.82, "A424")
    );
    private final ConcurrentHashMap<Integer, CopyOnWriteArrayList<Precipitacao>> precipitacoes = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    public InMemoryClimaRepository() {
        carregar(1, 55.0, "INMET", 1);
        carregar(1, 77.0, "CEMADEN", 2);
        carregar(1, 96.0, "PLUVIOMETRO", 3);
        carregar(1, 44.0, "INMET", 4);
        carregar(2, 48.0, "INMET", 1);
        carregar(2, 60.0, "PLUVIOMETRO", 2);
    }

    @Override
    public List<Localidade> listarLocalidades() {
        return localidades;
    }

    @Override
    public Optional<Localidade> buscarLocalidade(Integer id) {
        return localidades.stream().filter(localidade -> localidade.getId().equals(id)).findFirst();
    }

    @Override
    public List<Precipitacao> listarPrecipitacoes(Integer localidadeId) {
        return List.copyOf(precipitacoes.getOrDefault(localidadeId, new CopyOnWriteArrayList<>()));
    }

    @Override
    public Precipitacao salvarPrecipitacao(Precipitacao precipitacao) {
        if (precipitacao.getId() == null) {
            precipitacao.setId(sequencia.getAndIncrement());
        }
        precipitacoes.computeIfAbsent(precipitacao.getLocalidadeId(), key -> new CopyOnWriteArrayList<>())
                .add(precipitacao);
        return precipitacao;
    }

    private void carregar(Integer localidadeId, double mm, String fonte, int diasAtras) {
        Precipitacao precipitacao = new Precipitacao(
                sequencia.getAndIncrement(),
                localidadeId,
                mm,
                fonte,
                Instant.now().minusSeconds(diasAtras * 86400L)
        );
        precipitacoes.computeIfAbsent(localidadeId, key -> new CopyOnWriteArrayList<>()).add(precipitacao);
    }
}
