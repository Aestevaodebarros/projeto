package com.manejocaatingueiro.repository;

import com.manejocaatingueiro.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorEmail(String email);
    boolean existePorEmail(String email);
    Usuario salvar(Usuario usuario);
    List<Usuario> listar();
}
