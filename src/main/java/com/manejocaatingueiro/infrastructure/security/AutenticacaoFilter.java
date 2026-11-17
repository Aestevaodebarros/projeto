package com.manejocaatingueiro.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manejocaatingueiro.model.PerfilUsuario;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.service.JwtTokenService;
import com.manejocaatingueiro.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AutenticacaoFilter extends OncePerRequestFilter {
    public static final String USUARIO_ATRIBUTO = "usuarioAutenticado";

    private final JwtTokenService jwtTokenService;
    private final UsuarioService usuarioService;
    private final ObjectMapper objectMapper;

    public AutenticacaoFilter(JwtTokenService jwtTokenService, UsuarioService usuarioService,
                              ObjectMapper objectMapper) {
        this.jwtTokenService = jwtTokenService;
        this.usuarioService = usuarioService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String caminho = request.getRequestURI();
        if ("POST".equals(request.getMethod())
                && (caminho.equals("/api/v1/auth/register") || caminho.equals("/api/v1/auth/login"))) {
            return true;
        }
        if (!"GET".equals(request.getMethod())) {
            return false;
        }
        return caminho.equals("/api/v1/health")
                || caminho.equals("/api/v1/")
                || caminho.equals("/api/v1/localidades")
                || caminho.matches("/api/v1/localidades/\\d+/diagnostico")
                || caminho.matches("/api/v1/localidades/\\d+/precipitacoes")
                || caminho.equals("/api/v1/culturas");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        String token = authorization != null && authorization.startsWith("Bearer ")
                ? authorization.substring("Bearer ".length()).trim()
                : null;
        Long usuarioId = jwtTokenService.validar(token).orElse(null);
        Usuario usuario = usuarioId == null ? null : usuarioService.buscarPorId(usuarioId).orElse(null);
        if (usuario == null) {
            responder(response, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente, inválido ou expirado.");
            return;
        }

        request.setAttribute(USUARIO_ATRIBUTO, usuario);
        if (somenteAdmin(request) && usuario.getPerfil() != PerfilUsuario.ADMIN) {
            responder(response, HttpServletResponse.SC_FORBIDDEN,
                    "Somente administradores podem alterar parâmetros globais.");
            return;
        }
        if (requerPerfilPrivilegiado(request) && !temPerfilPrivilegiado(usuario)) {
            responder(response, HttpServletResponse.SC_FORBIDDEN,
                    "Seu perfil não tem permissão para executar esta operação.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean requerPerfilPrivilegiado(HttpServletRequest request) {
        String caminho = request.getRequestURI();
        return ("POST".equals(request.getMethod())
                && (caminho.matches("/api/v1/localidades/\\d+/clima/atualizar")
                || caminho.equals("/api/v1/alertas")))
                || ("PUT".equals(request.getMethod())
                && caminho.matches("/api/v1/solicitacoes-agua/\\d+/status"));
    }

    private boolean somenteAdmin(HttpServletRequest request) {
        return "PUT".equals(request.getMethod())
                && request.getRequestURI().equals("/api/v1/parametros-consumo");
    }

    private boolean temPerfilPrivilegiado(Usuario usuario) {
        PerfilUsuario perfil = usuario.getPerfil();
        return perfil == PerfilUsuario.ADMIN || perfil == PerfilUsuario.GESTOR_PUBLICO
                || perfil == PerfilUsuario.LIDER_COMUNITARIO;
    }

    private void responder(HttpServletResponse response, int status, String mensagem) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), Map.of("status", status, "mensagem", mensagem));
    }
}
