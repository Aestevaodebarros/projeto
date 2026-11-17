package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.Localidade;
import com.manejocaatingueiro.model.Precipitacao;
import com.manejocaatingueiro.model.ClimatologiaMensal;
import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.repository.ClimaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        ZoneId zona = ZoneId.of("America/Sao_Paulo");
        LocalDate hoje = LocalDate.now(zona);
        YearMonth mesAtual = YearMonth.from(hoje);
        Instant inicioMes = mesAtual.atDay(1).atStartOfDay(zona).toInstant();
        Instant inicioEstacao = (hoje.getMonthValue() >= 11
                ? LocalDate.of(hoje.getYear(), 11, 1)
                : LocalDate.of(hoje.getYear() - 1, 11, 1)).atStartOfDay(zona).toInstant();
        Instant inicioPeriodoRecente = hoje.minusDays(30).atStartOfDay(zona).toInstant();
        List<Precipitacao> precipitacoes = climaRepository.listarPrecipitacoes(localidadeId).stream()
                .filter(item -> item.getData() != null && item.getVolumeMm() != null)
                .filter(item -> !"DEMONSTRACAO".equals(item.getFonte()))
                .toList();
        List<Precipitacao> doMes = precipitacoes.stream()
                .filter(item -> !item.getData().isBefore(inicioMes))
                .toList();
        double acumuladoMes = doMes.stream().mapToDouble(Precipitacao::getVolumeMm).sum();
        double acumuladoEstacao = precipitacoes.stream()
                .filter(item -> !item.getData().isBefore(inicioEstacao))
                .mapToDouble(Precipitacao::getVolumeMm)
                .sum();
        double maiorChuvaRecente = precipitacoes.stream()
                .filter(item -> !item.getData().isBefore(inicioPeriodoRecente))
                .mapToDouble(Precipitacao::getVolumeMm)
                .max()
                .orElse(0.0);
        ClimatologiaMensal normal = climaRepository.listarClimatologia(localidadeId).stream()
                .filter(item -> Objects.equals(item.getMes(), hoje.getMonthValue()))
                .findFirst()
                .orElse(null);
        boolean dadosInsuficientes = normal == null || normal.getAnosBase() == null || normal.getAnosBase() < 10
                || doMes.isEmpty();
        double esperadoAteHoje = normal == null ? 0
                : normal.getPrecipitacaoMediaMm() * hoje.getDayOfMonth() / mesAtual.lengthOfMonth();
        String status = dadosInsuficientes
                ? "DADOS_INSUFICIENTES"
                : (acumuladoMes < esperadoAteHoje * 0.7 ? "alerta"
                : (acumuladoMes < esperadoAteHoje ? "atencao" : "normal"));
        String fontes = precipitacoes.stream().map(Precipitacao::getFonte)
                .filter(Objects::nonNull).distinct().sorted().reduce((a, b) -> a + ", " + b)
                .orElse("Sem dados observados");

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("localidadeId", localidadeId);
        resultado.put("chuvaAcumuladaMm", Math.round(acumuladoMes * 10.0) / 10.0);
        resultado.put("chuvaAcumuladaEstacaoMm", Math.round(acumuladoEstacao * 10.0) / 10.0);
        resultado.put("maiorChuvaRecenteMm", Math.round(maiorChuvaRecente * 10.0) / 10.0);
        resultado.put("mediaHistoricaMm", normal == null ? null : normal.getPrecipitacaoMediaMm());
        resultado.put("esperadoAteHojeMm", Math.round(esperadoAteHoje * 10.0) / 10.0);
        resultado.put("dadosInsuficientes", dadosInsuficientes);
        resultado.put("grauConfianca", dadosInsuficientes ? "INSUFICIENTE" : "MEDIA");
        resultado.put("status", status);
        resultado.put("fonte", fontes);
        resultado.put("atualizadoEm", Instant.now().toString());
        resultado.put("serie", doMes);
        return resultado;
    }

    public Precipitacao registrarPrecipitacao(Integer localidadeId, Double volumeMm, String fonte, LocalDate data) {
        if (climaRepository.buscarLocalidade(localidadeId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Localidade não encontrada.");
        }
        if (volumeMm == null || !Double.isFinite(volumeMm) || volumeMm < 0) {
            throw new IllegalArgumentException("O volume de chuva não pode ser negativo.");
        }
        if (fonte == null || !List.of("PLUVIOMETRO_PROPRIO", "ESTACAO", "ESTIMATIVA").contains(fonte)) {
            throw new IllegalArgumentException("Fonte deve ser PLUVIOMETRO_PROPRIO, ESTACAO ou ESTIMATIVA.");
        }
        if (data == null || data.isAfter(LocalDate.now(ZoneId.of("America/Sao_Paulo")))) {
            throw new IllegalArgumentException("A data da chuva deve ser hoje ou anterior.");
        }
        return climaRepository.salvarPrecipitacao(new Precipitacao(
                null,
                localidadeId,
                volumeMm,
                fonte,
                data.atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant()
        ));
    }

    public List<Precipitacao> listarPrecipitacoes(Integer localidadeId) {
        if (climaRepository.buscarLocalidade(localidadeId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Localidade não encontrada.");
        }
        return climaRepository.listarPrecipitacoes(localidadeId);
    }
}
