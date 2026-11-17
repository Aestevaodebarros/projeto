package com.manejocaatingueiro.repository;

import com.manejocaatingueiro.model.ParametrosConsumo;

import java.util.Optional;

public interface ParametrosConsumoRepository {
    Optional<ParametrosConsumo> buscar();
    ParametrosConsumo salvar(ParametrosConsumo parametros);
}
