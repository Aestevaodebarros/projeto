package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.ClimatologiaMensal;
import com.manejocaatingueiro.model.Localidade;
import com.manejocaatingueiro.model.Precipitacao;
import com.manejocaatingueiro.repository.ClimaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClimaIngestionService {
    private static final String FONTE_RECENTE = "OPEN_METEO";
    private static final String FONTE_CLIMATOLOGIA = "OPEN_METEO_ARCHIVE_10_ANOS";

    private final ClimaRepository climaRepository;
    private final OpenMeteoClient openMeteoClient;

    public ClimaIngestionService(ClimaRepository climaRepository, OpenMeteoClient openMeteoClient) {
        this.climaRepository = climaRepository;
        this.openMeteoClient = openMeteoClient;
    }

    @Scheduled(fixedDelayString = "${clima.ingestao.intervalo-ms:86400000}",
            initialDelayString = "${clima.ingestao.atraso-inicial-ms:600000}")
    public void atualizarTodasLocalidades() {
        for (Localidade localidade : climaRepository.listarLocalidades()) {
            atualizar(localidade.getId());
        }
    }

    public Map<String, Object> atualizar(Integer localidadeId) {
        Localidade localidade = climaRepository.buscarLocalidade(localidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Localidade não encontrada."));
        LocalDate hoje = LocalDate.now();
        List<OpenMeteoClient.ChuvaDiaria> recentes = openMeteoClient.buscarRecente(
                localidade,
                hoje.minusDays(14),
                hoje
        );
        recentes.forEach(dia -> climaRepository.salvarPrecipitacao(new Precipitacao(
                null,
                localidadeId,
                dia.volumeMm(),
                FONTE_RECENTE,
                dia.data().atStartOfDay(java.time.ZoneId.of("America/Sao_Paulo")).toInstant()
        )));

        int anoFinal = Year.now().getValue() - 1;
        LocalDate inicio = LocalDate.of(anoFinal - 9, 1, 1);
        LocalDate fim = LocalDate.of(anoFinal, 12, 31);
        Instant atualizadoEm = Instant.now();
        List<ClimatologiaMensal> normais = climaRepository.listarClimatologia(localidadeId);
        boolean precisaAtualizarClimatologia = normais.size() < 12
                || normais.stream().anyMatch(normal -> normal.getAtualizadoEm() == null
                || normal.getAtualizadoEm().isBefore(atualizadoEm.minusSeconds(30L * 86400)));
        Map<Integer, double[]> agregados = new HashMap<>();
        if (precisaAtualizarClimatologia) {
            openMeteoClient.buscarHistorico(localidade, inicio, fim).forEach(dia -> {
                double[] agregado = agregados.computeIfAbsent(dia.data().getMonthValue(), chave -> new double[2]);
                agregado[0] += dia.volumeMm();
                agregado[1]++;
            });
            agregados.forEach((mes, agregado) -> {
                if (agregado[1] > 0) {
                    ClimatologiaMensal normal = new ClimatologiaMensal();
                    normal.setLocalidadeId(localidadeId);
                    normal.setMes(mes);
                    normal.setPrecipitacaoMediaMm(agregado[0] / agregado[1] * 365.2425 / 12.0);
                    normal.setAnosBase(10);
                    normal.setFonte(FONTE_CLIMATOLOGIA);
                    normal.setAtualizadoEm(atualizadoEm);
                    climaRepository.salvarClimatologia(normal);
                }
            });
        }

        return Map.of(
                "localidadeId", localidadeId,
                "diasRecentesAtualizados", recentes.size(),
                "mesesClimatologicosAtualizados", agregados.size(),
                "climatologiaAtualizada", precisaAtualizarClimatologia,
                "fonte", "Open-Meteo",
                "atualizadoEm", atualizadoEm.toString()
        );
    }
}
