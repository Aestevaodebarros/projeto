package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public class JdbcUsuarioRepository implements UsuarioRepository {
    private static final String COLECAO = "usuarios";
    private final JdbcDocumentStore store;

    public JdbcUsuarioRepository(JdbcDocumentStore store) {
        this.store = store;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return store.buscar(COLECAO, id.toString(), Usuario.class);
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        String normalizado = normalizarEmail(email);
        return store.listar(COLECAO, Usuario.class).stream()
                .filter(usuario -> normalizarEmail(usuario.getEmail()).equals(normalizado))
                .findFirst();
    }

    @Override
    public boolean existePorEmail(String email) {
        return buscarPorEmail(email).isPresent();
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(store.proximoId(COLECAO));
        }
        return store.salvar(COLECAO, usuario.getId().toString(), usuario.getLocalidadeId(), usuario);
    }

    @Override
    public List<Usuario> listar() {
        return store.listar(COLECAO, Usuario.class).stream()
                .sorted(Comparator.comparing(Usuario::getId))
                .toList();
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
