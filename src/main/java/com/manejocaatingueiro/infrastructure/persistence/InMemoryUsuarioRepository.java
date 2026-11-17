package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUsuarioRepository implements UsuarioRepository {
    private final ConcurrentHashMap<Long, Usuario> usuarios = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong(1);

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        String emailNormalizado = normalizarEmail(email);
        return usuarios.values().stream()
                .filter(usuario -> normalizarEmail(usuario.getEmail()).equals(emailNormalizado))
                .findFirst();
    }

    @Override
    public boolean existePorEmail(String email) {
        return buscarPorEmail(email).isPresent();
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        if (usuario.getId() == null) {
            usuario.setId(sequencia.getAndIncrement());
        } else {
            sequencia.accumulateAndGet(usuario.getId() + 1, Math::max);
        }
        usuarios.put(usuario.getId(), usuario);
        return usuario;
    }

    @Override
    public List<Usuario> listar() {
        return usuarios.values().stream()
                .sorted(Comparator.comparing(Usuario::getId))
                .toList();
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
