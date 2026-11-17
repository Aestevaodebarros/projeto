package com.manejocaatingueiro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manejocaatingueiro.infrastructure.persistence.JdbcDocumentStore;
import com.manejocaatingueiro.model.Usuario;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtTokenService {
    private static final String COLECAO_CHAVES = "chaves_seguranca";
    private static final String ID_CHAVE = "jwt_hs256";
    private static final long DURACAO_SEGUNDOS = 12 * 60 * 60;

    private final JdbcDocumentStore store;
    private final ObjectMapper objectMapper;
    private byte[] chave;

    public JwtTokenService(JdbcDocumentStore store, ObjectMapper objectMapper) {
        this.store = store;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    synchronized void inicializarChave() {
        String material = store.buscar(COLECAO_CHAVES, ID_CHAVE, String.class).orElseGet(() -> {
            byte[] aleatorio = new byte[32];
            new SecureRandom().nextBytes(aleatorio);
            String gerado = Base64.getEncoder().encodeToString(aleatorio);
            store.salvar(COLECAO_CHAVES, ID_CHAVE, null, gerado);
            return gerado;
        });
        chave = Base64.getDecoder().decode(material);
    }

    public String criarToken(Usuario usuario) {
        long agora = Instant.now().getEpochSecond();
        String cabecalho = codificarJson(Map.of("alg", "HS256", "typ", "JWT"));
        String carga = codificarJson(Map.of(
                "sub", usuario.getId().toString(),
                "iat", agora,
                "exp", agora + DURACAO_SEGUNDOS
        ));
        String mensagem = cabecalho + "." + carga;
        try {
            return mensagem + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(assinar(mensagem));
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Não foi possível assinar o token de autenticação.", exception);
        }
    }

    public Optional<Long> validar(String token) {
        if (token == null) {
            return Optional.empty();
        }
        String[] partes = token.split("\\.", -1);
        if (partes.length != 3 || chave == null) {
            return Optional.empty();
        }
        try {
            String mensagem = partes[0] + "." + partes[1];
            byte[] assinaturaRecebida = Base64.getUrlDecoder().decode(partes[2]);
            if (!MessageDigest.isEqual(assinar(mensagem), assinaturaRecebida)) {
                return Optional.empty();
            }
            JsonNode cabecalho = objectMapper.readTree(Base64.getUrlDecoder().decode(partes[0]));
            JsonNode carga = objectMapper.readTree(Base64.getUrlDecoder().decode(partes[1]));
            long expiraEm = carga.path("exp").asLong(0);
            long usuarioId = carga.path("sub").asLong(0);
            if (!"HS256".equals(cabecalho.path("alg").asText())
                    || expiraEm <= Instant.now().getEpochSecond()
                    || usuarioId <= 0) {
                return Optional.empty();
            }
            return Optional.of(usuarioId);
        } catch (IllegalArgumentException | IOException | GeneralSecurityException exception) {
            return Optional.empty();
        }
    }

    private String codificarJson(Object valor) {
        try {
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(objectMapper.writeValueAsBytes(valor));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível gerar o token de autenticação.", exception);
        }
    }

    private byte[] assinar(String mensagem) throws GeneralSecurityException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(chave, "HmacSHA256"));
        return mac.doFinal(mensagem.getBytes(StandardCharsets.UTF_8));
    }
}
