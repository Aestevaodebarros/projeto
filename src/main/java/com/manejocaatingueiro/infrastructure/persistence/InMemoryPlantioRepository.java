package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.Cultura;
import com.manejocaatingueiro.repository.PlantioRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPlantioRepository implements PlantioRepository {
    private final List<Cultura> culturas = List.of(
            new Cultura(1, "Milho", 650.0, 400.0, "Embrapa / ZARC", 120),
            new Cultura(2, "Feijão-Caupi", 400.0, 300.0, "Souza et al. (2015)", 90),
            new Cultura(3, "Mamona", 650.0, 430.0, "Embrapa", 140)
    );
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Double>> limiares = new ConcurrentHashMap<>();

    @Override
    public List<Cultura> listarCulturas() {
        return culturas;
    }

    @Override
    public Optional<Cultura> buscarCultura(Integer culturaId) {
        return culturas.stream().filter(cultura -> cultura.getId().equals(culturaId)).findFirst();
    }

    @Override
    public Optional<Double> buscarLimiar(Integer culturaId, Long usuarioId) {
        return Optional.ofNullable(limiares.getOrDefault(culturaId, new ConcurrentHashMap<>()).get(usuarioId));
    }

    @Override
    public void salvarLimiar(Integer culturaId, Long usuarioId, Double limiarMm) {
        limiares.computeIfAbsent(culturaId, key -> new ConcurrentHashMap<>()).put(usuarioId, limiarMm);
    }
}
