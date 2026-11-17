package com.manejocaatingueiro.service;

import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.model.AlertaClimatico;
import com.manejocaatingueiro.repository.AlertaRepository;
import com.manejocaatingueiro.repository.ClimaRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AlertaService {
    private final AlertaRepository alertaRepository;
    private final ClimaRepository climaRepository;
    private final ClimaService climaService;

    public AlertaService(AlertaRepository alertaRepository, ClimaRepository climaRepository,
                         ClimaService climaService) {
        this.alertaRepository = alertaRepository;
        this.climaRepository = climaRepository;
        this.climaService = climaService;
    }

    public List<AlertaClimatico> consultarAtivos(Integer localidadeId) {
        if (climaRepository.buscarLocalidade(localidadeId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Localidade não encontrada.");
        }
        atualizarAlertaDeChuva(localidadeId);
        Instant agora = Instant.now();
        return alertaRepository.listarPorLocalidade(localidadeId).stream()
                .filter(AlertaClimatico::isAtivo)
                .filter(alerta -> alerta.getValidoAte() == null || alerta.getValidoAte().isAfter(agora))
                .toList();
    }

    public AlertaClimatico publicar(Integer localidadeId, String tipo, String severidade,
                                    String mensagem, String fonte) {
        if (climaRepository.buscarLocalidade(localidadeId).isEmpty()) {
            throw new RecursoNaoEncontradoException("Localidade não encontrada.");
        }
        if (vazio(tipo) || vazio(mensagem) || severidade == null
                || !List.of("INFORMATIVO", "ATENCAO", "CRITICO").contains(severidade)) {
            throw new IllegalArgumentException("Tipo, mensagem e severidade válida são obrigatórios.");
        }
        AlertaClimatico alerta = new AlertaClimatico();
        alerta.setLocalidadeId(localidadeId);
        alerta.setTipo(tipo.trim());
        alerta.setSeveridade(severidade);
        alerta.setMensagem(mensagem.trim());
        alerta.setFonte(fonte == null || fonte.isBlank() ? "Operação local" : fonte.trim());
        alerta.setCriadoEm(Instant.now());
        alerta.setValidoAte(Instant.now().plus(24, ChronoUnit.HOURS));
        alerta.setAtivo(true);
        return alertaRepository.salvar(alerta);
    }

    public AlertaClimatico alertarRiscoSafra(Integer localidadeId, Integer culturaId, String mensagem) {
        String tipo = "RISCO_SAFRA_" + culturaId;
        Instant agora = Instant.now();
        AlertaClimatico existente = alertaRepository.listarPorLocalidade(localidadeId).stream()
                .filter(AlertaClimatico::isAtivo)
                .filter(alerta -> tipo.equals(alerta.getTipo()))
                .filter(alerta -> alerta.getCriadoEm() != null
                        && alerta.getCriadoEm().isAfter(agora.minus(24, ChronoUnit.HOURS)))
                .findFirst()
                .orElse(null);
        if (existente != null) {
            return existente;
        }
        return publicar(localidadeId, tipo, "CRITICO", mensagem, "RF21 — estimativa de risco de safra");
    }

    private void atualizarAlertaDeChuva(Integer localidadeId) {
        var diagnostico = climaService.diagnostico(localidadeId);
        String status = String.valueOf(diagnostico.get("status"));
        if (!"alerta".equals(status)) {
            return;
        }
        Instant agora = Instant.now();
        boolean existente = alertaRepository.listarPorLocalidade(localidadeId).stream()
                .anyMatch(alerta -> alerta.isAtivo() && "CHUVA_ABAIXO_DO_ESPERADO".equals(alerta.getTipo())
                        && alerta.getCriadoEm() != null
                        && alerta.getCriadoEm().isAfter(agora.minus(24, ChronoUnit.HOURS)));
        if (!existente) {
            publicar(localidadeId, "CHUVA_ABAIXO_DO_ESPERADO", "ATENCAO",
                    "A chuva observada está abaixo do esperado para este período. Confira a fonte e as condições da sua roça.",
                    String.valueOf(diagnostico.get("fonte")));
        }
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
