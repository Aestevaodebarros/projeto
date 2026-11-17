package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.Localidade;
import com.manejocaatingueiro.model.Precipitacao;
import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.repository.ClimaRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClimaService {
    private final ClimaRepository climaRepository;

    public ClimaService(ClimaRepository climaRepository) {
        this.climaRepository = climaRepository;
    }

    public List<Localidade> listarLocalidades() {
        return climaRepository.listarLocalidades();
    }

    public Map<String, Object> diagnostico(Integer localidadeId) {
        if (climaRepository.buscarLocalidade(localidadeId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Localidade não encontrada.");
        }

        List<Precipitacao> precipitacoes = climaRepository.listarPrecipitacoes(localidadeId);
        double acumuladoMes = precipitacoes.stream().mapToDouble(Precipitacao::getVolumeMm).sum();
        double mediaHistorica = 560.0;
        String status = acumuladoMes >= mediaHistorica
                ? "normal"
                : (acumuladoMes >= mediaHistorica * 0.7 ? "atencao" : "alerta");

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("localidadeId", localidadeId);
        resultado.put("chuvaAcumuladaMm", Math.round(acumuladoMes * 10.0) / 10.0);
        resultado.put("mediaHistoricaMm", mediaHistorica);
        resultado.put("status", status);
        resultado.put("fonte", "INMET + pluviômetro local");
        resultado.put("atualizadoEm", Instant.now().toString());
        resultado.put("serie", precipitacoes);
        return resultado;
    }
}
