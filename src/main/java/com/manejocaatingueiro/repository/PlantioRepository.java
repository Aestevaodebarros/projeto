package com.manejocaatingueiro.repository;

import com.manejocaatingueiro.model.Cultura;

import java.util.List;
import java.util.Optional;

public interface PlantioRepository {
    List<Cultura> listarCulturas();
    Optional<Cultura> buscarCultura(Integer culturaId);
    Optional<Double> buscarLimiar(Integer culturaId, Long usuarioId);
    void salvarLimiar(Integer culturaId, Long usuarioId, Double limiarMm);
}
