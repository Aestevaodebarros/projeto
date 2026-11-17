package com.manejocaatingueiro.infrastructure.persistence;

import com.manejocaatingueiro.model.Cultura;
import com.manejocaatingueiro.model.FaseCultura;
import com.manejocaatingueiro.model.LimiarPlantio;
import com.manejocaatingueiro.repository.PlantioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcPlantioRepository implements PlantioRepository {
    private static final String CULTURAS = "culturas";
    private static final String LIMIARES = "limiares_plantio";
    private final JdbcDocumentStore store;

    public JdbcPlantioRepository(JdbcDocumentStore store) {
        this.store = store;
    }

    @PostConstruct
    void inicializarCulturasDemonstrativas() {
        if (store.listar(CULTURAS, Cultura.class).isEmpty()) {
            Cultura milho = new Cultura(1, "Milho", 650.0, 400.0,
                    "Kc: Souza et al. (2015); demanda: Embrapa/ZARC; duração das fases provisória, validar", 120);
            milho.setFases(List.of(
                    new FaseCultura("Inicial", 0.86, 25),
                    new FaseCultura("Desenvolvimento", 1.23, 35),
                    new FaseCultura("Meio do ciclo", 0.97, 35),
                    new FaseCultura("Final", 0.52, 25)
            ));
            Cultura feijao = new Cultura(2, "Feijão-Caupi", 400.0, 300.0,
                    "Kc: Souza et al. (2015); demanda: estudo de Apodi (RN); duração das fases provisória, validar", 90);
            feijao.setFases(List.of(
                    new FaseCultura("Inicial", 0.68, 20),
                    new FaseCultura("Desenvolvimento", 1.02, 25),
                    new FaseCultura("Meio do ciclo", 1.06, 25),
                    new FaseCultura("Final", 0.63, 20)
            ));
            Cultura mamona = new Cultura(3, "Mamona", 650.0, 430.0,
                    "Embrapa; referência de 600–700 mm/ciclo; parâmetros locais precisam de validação", 140);
            List.of(milho, feijao, mamona)
                    .forEach(cultura -> store.salvar(CULTURAS, cultura.getId().toString(), null, cultura));
        }
    }

    @Override
    public List<Cultura> listarCulturas() {
        return store.listar(CULTURAS, Cultura.class).stream()
                .sorted(Comparator.comparing(Cultura::getId))
                .toList();
    }

    @Override
    public Optional<Cultura> buscarCultura(Integer culturaId) {
        return store.buscar(CULTURAS, culturaId.toString(), Cultura.class);
    }

    @Override
    public Optional<Double> buscarLimiar(Integer culturaId, Long usuarioId) {
        return store.buscar(LIMIARES, culturaId + "_" + usuarioId, LimiarPlantio.class)
                .map(LimiarPlantio::getLimiarMm);
    }

    @Override
    public void salvarLimiar(Integer culturaId, Long usuarioId, Double limiarMm) {
        LimiarPlantio limiar = new LimiarPlantio(culturaId, usuarioId, limiarMm);
        store.salvar(LIMIARES, limiar.getId(), null, limiar);
    }
}
