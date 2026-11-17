package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.Localidade;
import com.manejocaatingueiro.model.Precipitacao;
import com.manejocaatingueiro.model.ClimatologiaMensal;
import com.manejocaatingueiro.repository.ClimaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcClimaRepository implements ClimaRepository {
    private static final String LOCALIDADES = "localidades";
    private static final String PRECIPITACOES = "precipitacoes";
    private static final String CLIMATOLOGIA = "climatologia_mensal";
    private final JdbcDocumentStore store;

    public JdbcClimaRepository(JdbcDocumentStore store) {
        this.store = store;
    }

    @PostConstruct
    void inicializarDadosDemonstrativos() {
        if (store.listar(LOCALIDADES, Localidade.class).isEmpty()) {
            List.of(
                    new Localidade(1, "Irecê", "MUNICIPIO", null, "2914604", "BA", -11.30, -41.85, "A424"),
                    new Localidade(2, "Presidente Dutra", "MUNICIPIO", null, "2925709", "BA", -11.29, -41.98, "A424"),
                    new Localidade(3, "Povoado de Achado (Irecê)", "POVOADO", 1, "2914604", "BA", -11.26, -41.82, "A424")
            ).forEach(localidade -> store.salvar(LOCALIDADES, localidade.getId().toString(), null, localidade));
        }
        if (store.listar(PRECIPITACOES, Precipitacao.class).isEmpty()) {
            carregar(1, 55.0, "DEMONSTRACAO", 1);
            carregar(1, 77.0, "DEMONSTRACAO", 2);
            carregar(1, 96.0, "DEMONSTRACAO", 3);
            carregar(1, 44.0, "DEMONSTRACAO", 4);
            carregar(2, 48.0, "DEMONSTRACAO", 1);
            carregar(2, 60.0, "DEMONSTRACAO", 2);
        }
    }

    @Override
    public List<Localidade> listarLocalidades() {
        return store.listar(LOCALIDADES, Localidade.class).stream()
                .sorted(Comparator.comparing(Localidade::getId))
                .toList();
    }

    @Override
    public Optional<Localidade> buscarLocalidade(Integer id) {
        return store.buscar(LOCALIDADES, id.toString(), Localidade.class);
    }

    @Override
    public List<Precipitacao> listarPrecipitacoes(Integer localidadeId) {
        return store.listarPorLocalidade(PRECIPITACOES, localidadeId, Precipitacao.class).stream()
                .sorted(Comparator.comparing(Precipitacao::getData))
                .toList();
    }

    @Override
    public Precipitacao salvarPrecipitacao(Precipitacao precipitacao) {
        if (precipitacao.getData() != null && "OPEN_METEO".equals(precipitacao.getFonte())) {
            Optional<Precipitacao> existente = listarPrecipitacoes(precipitacao.getLocalidadeId()).stream()
                    .filter(item -> precipitacao.getFonte().equals(item.getFonte()))
                    .filter(item -> item.getData().atZone(java.time.ZoneOffset.UTC).toLocalDate()
                            .equals(precipitacao.getData().atZone(java.time.ZoneOffset.UTC).toLocalDate()))
                    .findFirst();
            if (existente.isPresent()) {
                precipitacao.setId(existente.get().getId());
            }
        }
        if (precipitacao.getId() == null) {
            precipitacao.setId(store.proximoId(PRECIPITACOES));
        }
        return store.salvar(
                PRECIPITACOES,
                precipitacao.getId().toString(),
                precipitacao.getLocalidadeId(),
                precipitacao
        );
    }

    private void carregar(Integer localidadeId, double mm, String fonte, int diasAtras) {
        Precipitacao precipitacao = new Precipitacao(
                store.proximoId(PRECIPITACOES),
                localidadeId,
                mm,
                fonte,
                Instant.now().minusSeconds(diasAtras * 86400L)
        );
        salvarPrecipitacao(precipitacao);
    }

    @Override
    public List<ClimatologiaMensal> listarClimatologia(Integer localidadeId) {
        return store.listarPorLocalidade(CLIMATOLOGIA, localidadeId, ClimatologiaMensal.class).stream()
                .sorted(Comparator.comparing(ClimatologiaMensal::getMes))
                .toList();
    }

    @Override
    public void salvarClimatologia(ClimatologiaMensal climatologia) {
        String id = climatologia.getLocalidadeId() + "_" + climatologia.getMes();
        store.salvar(CLIMATOLOGIA, id, climatologia.getLocalidadeId(), climatologia);
    }
}
