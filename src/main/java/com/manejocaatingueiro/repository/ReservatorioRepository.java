package com.manejocaatingueiro.repository;

import com.manejocaatingueiro.model.Reservatorio;

import java.util.List;
import java.util.Optional;

public interface ReservatorioRepository {
    List<Reservatorio> listarPorLocalidade(Integer localidadeId);
    List<Reservatorio> listarPorUsuario(Long usuarioId);
    Optional<Reservatorio> buscarPorId(Long id);
    Reservatorio salvar(Reservatorio reservatorio);
}
