package com.manejocaatingueiro.repository;

import com.manejocaatingueiro.model.RegistroSafra;

import java.util.List;
import java.util.Optional;

public interface RegistroSafraRepository {
    List<RegistroSafra> listarPorUsuario(Long usuarioId);
    Optional<RegistroSafra> buscarPorId(Long id);
    RegistroSafra salvar(RegistroSafra registro);
}
