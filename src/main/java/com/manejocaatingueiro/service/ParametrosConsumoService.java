package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.ParametrosConsumo;
import com.manejocaatingueiro.repository.ParametrosConsumoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ParametrosConsumoService {
    private final ParametrosConsumoRepository repository;

    public ParametrosConsumoService(ParametrosConsumoRepository repository) {
        this.repository = repository;
    }

    public ParametrosConsumo consultar() {
        return repository.buscar().orElseGet(ParametrosConsumo::new);
    }

    public ParametrosConsumo atualizar(ParametrosConsumo parametros) {
        if (parametros == null
                || !valorValido(parametros.getBovinosLitrosDia(), 50, 53)
                || !valorValido(parametros.getCaprinosOvinosLitrosDia(), 0.01, 100)
                || !valorValido(parametros.getAvesLitrosDia(), 0.01, 5)
                || !valorValido(parametros.getEquinosLitrosDia(), 0.01, 100)
                || !valorValido(parametros.getSuinosLitrosDia(), 0.01, 100)
                || !valorValido(parametros.getIrrigacaoLitrosM2Dia(), 0.01, 100)
                || parametros.getFonte() == null || parametros.getFonte().isBlank()) {
            throw new IllegalArgumentException("Parâmetros de consumo inválidos; informe fonte e valores dentro dos limites.");
        }
        parametros.setVersao("v" + (Integer.parseInt(consultar().getVersao().substring(1)) + 1));
        parametros.setAtualizadoEm(Instant.now());
        return repository.salvar(parametros);
    }

    private boolean valorValido(Double valor, double minimo, double maximo) {
        return valor != null && Double.isFinite(valor) && valor >= minimo && valor <= maximo;
    }
}
