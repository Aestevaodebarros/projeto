package com.manejocaatingueiro.repository;

import com.manejocaatingueiro.model.Localidade;
import com.manejocaatingueiro.model.Precipitacao;

import java.util.List;
import java.util.Optional;

public interface ClimaRepository {
    List<Localidade> listarLocalidades();
    Optional<Localidade> buscarLocalidade(Integer id);
    List<Precipitacao> listarPrecipitacoes(Integer localidadeId);
    Precipitacao salvarPrecipitacao(Precipitacao precipitacao);
}
