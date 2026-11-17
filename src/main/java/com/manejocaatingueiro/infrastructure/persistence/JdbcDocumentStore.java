package com.manejocaatingueiro.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JdbcDocumentStore {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public JdbcDocumentStore(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public synchronized long proximoId(String colecao) {
        List<Long> valores = jdbcTemplate.query(
                "SELECT next_value FROM domain_sequences WHERE collection_name = ? FOR UPDATE",
                (resultSet, rowNumber) -> resultSet.getLong("next_value"),
                colecao
        );
        if (valores.isEmpty()) {
            jdbcTemplate.update(
                    "INSERT INTO domain_sequences (collection_name, next_value) VALUES (?, ?)",
                    colecao,
                    2L
            );
            return 1L;
        }

        long id = valores.get(0);
        jdbcTemplate.update(
                "UPDATE domain_sequences SET next_value = ? WHERE collection_name = ?",
                id + 1,
                colecao
        );
        return id;
    }

    public <T> Optional<T> buscar(String colecao, String id, Class<T> tipo) {
        List<String> payloads = jdbcTemplate.query(
                "SELECT payload FROM domain_documents WHERE collection_name = ? AND document_id = ?",
                (resultSet, rowNumber) -> resultSet.getString("payload"),
                colecao,
                id
        );
        return payloads.stream().findFirst().map(payload -> desserializar(payload, tipo));
    }

    public <T> List<T> listar(String colecao, Class<T> tipo) {
        return jdbcTemplate.query(
                "SELECT payload FROM domain_documents WHERE collection_name = ? ORDER BY document_id",
                (resultSet, rowNumber) -> desserializar(resultSet.getString("payload"), tipo),
                colecao
        );
    }

    public <T> List<T> listarPorLocalidade(String colecao, Integer localidadeId, Class<T> tipo) {
        return jdbcTemplate.query(
                "SELECT payload FROM domain_documents WHERE collection_name = ? AND localidade_id = ? ORDER BY document_id",
                (resultSet, rowNumber) -> desserializar(resultSet.getString("payload"), tipo),
                colecao,
                localidadeId
        );
    }

    public <T> T salvar(String colecao, String id, Integer localidadeId, T documento) {
        try {
            jdbcTemplate.update(
                    """
                    MERGE INTO domain_documents (collection_name, document_id, localidade_id, payload, updated_at)
                    KEY (collection_name, document_id) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
                    """,
                    colecao,
                    id,
                    localidadeId,
                    objectMapper.writeValueAsString(documento)
            );
            return documento;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Não foi possível serializar o documento " + colecao + "/" + id, exception);
        }
    }

    private <T> T desserializar(String payload, Class<T> tipo) {
        try {
            return objectMapper.readValue(payload, tipo);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Documento persistido inválido para " + tipo.getSimpleName(), exception);
        }
    }
}
