package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.FonteAgua;
import com.manejocaatingueiro.model.PerfilProdutivo;
import com.manejocaatingueiro.model.PerfilUsuario;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.domain.exception.ConflitoDeCadastroException;
import com.manejocaatingueiro.domain.exception.CredenciaisInvalidasException;
import com.manejocaatingueiro.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        if (usuarioRepository.listar().isEmpty()) {
            usuarioRepository.salvar(criarUsuarioDemo());
        }
    }

    public Usuario registrar(Usuario usuario) {
        if (usuario == null
                || vazio(usuario.getNome())
                || vazio(usuario.getEmail())
                || vazio(usuario.getTelefone())) {
            throw new IllegalArgumentException("Nome, e-mail e telefone são obrigatórios.");
        }

        String email = usuario.getEmail().trim().toLowerCase(Locale.ROOT);
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Informe um endereço de e-mail válido.");
        }
        if (usuarioRepository.existePorEmail(email)) {
            throw new ConflitoDeCadastroException("Já existe um usuário com esse e-mail.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(usuario.getNome().trim());
        novoUsuario.setEmail(email);
        novoUsuario.setTelefone(usuario.getTelefone().trim());
        novoUsuario.setPerfil(usuario.getPerfil() == null ? PerfilUsuario.AGRICULTOR : usuario.getPerfil());
        novoUsuario.setAbastecimento(usuario.getAbastecimento() == null
                ? FonteAgua.CISTERNA
                : usuario.getAbastecimento());
        novoUsuario.setLocalidadeId(usuario.getLocalidadeId() == null ? 1 : usuario.getLocalidadeId());
        novoUsuario.setPerfilProdutivo(usuario.getPerfilProdutivo() == null
                ? new PerfilProdutivo()
                : usuario.getPerfilProdutivo());
        novoUsuario.setEmailVerificado(false);
        if (!vazio(usuario.getSenhaHash())) {
            novoUsuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        }
        return usuarioRepository.salvar(novoUsuario);
    }

    public Usuario autenticar(String email, String senha) {
        if (vazio(email) || vazio(senha)) {
            throw new IllegalArgumentException("E-mail e senha são obrigatórios.");
        }

        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);
        if (usuario.getSenhaHash() == null || !passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        return usuario;
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id);
    }

    public List<Usuario> listar() {
        return usuarioRepository.listar();
    }

    private Usuario criarUsuarioDemo() {
        Usuario demo = new Usuario();
        demo.setNome("Agricultor Demo");
        demo.setEmail("demo@manejocaatingueiro.org.br");
        demo.setTelefone("75999990000");
        demo.setPerfil(PerfilUsuario.AGRICULTOR);
        demo.setAbastecimento(FonteAgua.CISTERNA);
        demo.setSenhaHash(passwordEncoder.encode("123456"));
        demo.setLocalidadeId(1);
        demo.setPerfilProdutivo(new PerfilProdutivo(4, 2, 5, 20, 300));
        demo.setEmailVerificado(true);
        return demo;
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
