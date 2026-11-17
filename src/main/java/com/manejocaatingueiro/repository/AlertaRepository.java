package com.manejocaatingueiro.repository;

import com.manejocaatingueiro.model.AlertaClimatico;

import java.util.List;

public interface AlertaRepository {
    List<AlertaClimatico> listarPorLocalidade(Integer localidadeId);
    AlertaClimatico salvar(AlertaClimatico alerta);
}
