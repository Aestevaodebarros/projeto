package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.model.RegistroNivelReservatorio;
import com.manejocaatingueiro.model.ChecklistSanitario;
import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.repository.ReservatorioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservatorioService {
    private final ReservatorioRepository reservatorioRepository;

    public ReservatorioService(ReservatorioRepository reservatorioRepository) {
        this.reservatorioRepository = reservatorioRepository;
    }

    public List<Reservatorio> listarPorLocalidade(Integer localidadeId) {
        return reservatorioRepository.listarPorLocalidade(localidadeId);
    }

    public List<Reservatorio> listarPorUsuario(Long usuarioId) {
        return reservatorioRepository.listarPorUsuario(usuarioId);
    }

    public Reservatorio atualizarNivel(Long reservatorioId, Long usuarioId, Double nivelPercentual) {
        return atualizarNivel(reservatorioId, usuarioId, nivelPercentual, null, null);
    }

    public Reservatorio atualizarNivel(Long reservatorioId, Long usuarioId, Double nivelPercentual,
                                       String nivelQualitativo, Double profundidadeLencolM) {
        Reservatorio reservatorio = buscarDoResponsavel(reservatorioId, usuarioId);
        Double percentualCalculado = nivelPercentual;
        String qualitativo = normalizarQualitativo(nivelQualitativo);
        if (percentualCalculado != null && qualitativo != null) {
            throw new IllegalArgumentException("Informe o nível em percentual ou qualitativo, não ambos.");
        }
        if (qualitativo != null && !java.util.Set.of("CHEIO", "MEDIO", "BAIXO", "CRITICO").contains(qualitativo)) {
            throw new IllegalArgumentException("Nível qualitativo deve ser CHEIO, MEDIO, BAIXO ou CRITICO.");
        }
        if (percentualCalculado == null && qualitativo != null) {
            percentualCalculado = switch (qualitativo) {
                case "CHEIO" -> 90.0;
                case "MEDIO" -> 60.0;
                case "BAIXO" -> 30.0;
                case "CRITICO" -> 10.0;
                default -> throw new IllegalArgumentException("Nível qualitativo inválido.");
            };
        }
        if (percentualCalculado == null || !Double.isFinite(percentualCalculado)
                || percentualCalculado < 0 || percentualCalculado > 100) {
            throw new IllegalArgumentException("Informe nível em percentual (0–100) ou escala qualitativa válida.");
        }
        if (profundidadeLencolM != null && (!Double.isFinite(profundidadeLencolM) || profundidadeLencolM < 0)) {
            throw new IllegalArgumentException("A profundidade do lençol deve ser um valor positivo.");
        }
        if ("POCO".equalsIgnoreCase(reservatorio.getTipo()) && profundidadeLencolM != null
                && reservatorio.getProfundidadeTotalM() != null
                && profundidadeLencolM > reservatorio.getProfundidadeTotalM()) {
            throw new IllegalArgumentException("A profundidade do lençol não pode exceder a profundidade total do poço.");
        }
        reservatorio.setNivelPercentual(percentualCalculado);
        reservatorio.setNivelQualitativo(qualitativo);
        if (profundidadeLencolM != null) {
            reservatorio.setProfundidadeLençolM(profundidadeLencolM);
        }
        Instant agora = Instant.now();
        if (reservatorio.getHistoricoNiveis() == null) {
            reservatorio.setHistoricoNiveis(new java.util.ArrayList<>());
        }
        LocalDate hoje = LocalDate.now(java.time.ZoneId.of("America/Sao_Paulo"));
        reservatorio.getHistoricoNiveis().removeIf(registro -> hoje.equals(registro.getData()));
        reservatorio.getHistoricoNiveis().add(new RegistroNivelReservatorio(
                hoje, percentualCalculado, qualitativo, reservatorio.getProfundidadeLençolM(), agora));
        reservatorio.setUltimaSincronizacao(agora);
        return reservatorioRepository.salvar(reservatorio);
    }

    public Reservatorio salvar(Reservatorio reservatorio, Long usuarioId) {
        if (reservatorio == null
                || reservatorio.getNome() == null || reservatorio.getNome().isBlank()
                || reservatorio.getTipo() == null || reservatorio.getLocalidadeId() == null) {
            throw new IllegalArgumentException("Nome, tipo e localidade são obrigatórios para o reservatório.");
        }
        if (reservatorio.getNivelPercentual() != null
                && (!Double.isFinite(reservatorio.getNivelPercentual())
                || reservatorio.getNivelPercentual() < 0 || reservatorio.getNivelPercentual() > 100)) {
            throw new IllegalArgumentException("Nível do reservatório deve estar entre 0 e 100%.");
        }
        if (reservatorio.getCapacidadeLitros() != null
                && (!Double.isFinite(reservatorio.getCapacidadeLitros()) || reservatorio.getCapacidadeLitros() <= 0)) {
            throw new IllegalArgumentException("A capacidade do reservatório deve ser maior que zero.");
        }
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("Responsável pelo reservatório inválido.");
        }
        reservatorio.setUsuarioResponsavelId(usuarioId);
        reservatorio.setUltimaSincronizacao(Instant.now());
        return reservatorioRepository.salvar(reservatorio);
    }

    public void registrarChecklist(Long reservatorioId, Long usuarioId, String item, boolean conforme) {
        if (item == null || item.isBlank()) {
            throw new IllegalArgumentException("Informe o item do checklist sanitário.");
        }
        Reservatorio reservatorio = buscarDoResponsavel(reservatorioId, usuarioId);
        ChecklistSanitario checklist = new ChecklistSanitario();
        checklist.setId(System.currentTimeMillis());
        checklist.setItem(item.trim());
        checklist.setConforme(conforme);
        checklist.setDataAvaliacao(Instant.now());
        reservatorio.getChecklists().add(checklist);
        reservatorio.setUltimaSincronizacao(Instant.now());
        reservatorioRepository.salvar(reservatorio);
    }

    private Reservatorio buscarDoResponsavel(Long reservatorioId, Long usuarioId) {
        return reservatorioRepository.buscarPorId(reservatorioId)
                .filter(reservatorio -> usuarioId != null
                        && usuarioId.equals(reservatorio.getUsuarioResponsavelId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reservatório não encontrado."));
    }

    private String normalizarQualitativo(String nivelQualitativo) {
        return nivelQualitativo == null || nivelQualitativo.isBlank()
                ? null : nivelQualitativo.trim().toUpperCase(java.util.Locale.ROOT);
    }
}
