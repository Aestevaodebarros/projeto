package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.repository.ReservatorioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    public Reservatorio atualizarNivel(Long reservatorioId, Double nivelPercentual) {
        if (nivelPercentual == null || !Double.isFinite(nivelPercentual)
                || nivelPercentual < 0 || nivelPercentual > 100) {
            throw new IllegalArgumentException("Nível do reservatório deve estar entre 0 e 100%.");
        }
        Reservatorio reservatorio = reservatorioRepository.buscarPorId(reservatorioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reservatório não encontrado."));
        reservatorio.setNivelPercentual(nivelPercentual);
        reservatorio.setUltimaSincronizacao(Instant.now());
        return reservatorioRepository.salvar(reservatorio);
    }

    public Reservatorio salvar(Reservatorio reservatorio) {
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
        reservatorio.setUltimaSincronizacao(Instant.now());
        return reservatorioRepository.salvar(reservatorio);
    }

    public void registrarChecklist(Long reservatorioId, String item, boolean conforme) {
        if (item == null || item.isBlank()) {
            throw new IllegalArgumentException("Informe o item do checklist sanitário.");
        }
        Reservatorio reservatorio = reservatorioRepository.buscarPorId(reservatorioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reservatório não encontrado."));
        Reservatorio.ChecklistSanitario checklist = new Reservatorio.ChecklistSanitario();
        checklist.setId(System.currentTimeMillis());
        checklist.setItem(item.trim());
        checklist.setConforme(conforme);
        checklist.setData(Instant.now());
        reservatorio.getChecklists().add(checklist);
        reservatorio.setUltimaSincronizacao(Instant.now());
        reservatorioRepository.salvar(reservatorio);
    }
}
