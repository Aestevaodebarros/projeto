package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.AvisoComunitario;
import com.manejocaatingueiro.model.MonitorComunitario;
import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.repository.AvisoComunitarioRepository;
import com.manejocaatingueiro.repository.ReservatorioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
public class ComunidadService {
    private static final int MINIMO_PARTICIPANTES_AGREGADO = 5;
    private final AvisoComunitarioRepository avisoRepository;
    private final ReservatorioRepository reservatorioRepository;

    public ComunidadService(AvisoComunitarioRepository avisoRepository,
                            ReservatorioRepository reservatorioRepository) {
        this.avisoRepository = avisoRepository;
        this.reservatorioRepository = reservatorioRepository;
    }

    public List<AvisoComunitario> listar(Integer localidadeId) {
        Instant limite = Instant.now().minus(30, ChronoUnit.DAYS);
        return avisoRepository.listarPorLocalidade(localidadeId).stream()
                .filter(aviso -> aviso.getCriadoEm() != null && !aviso.getCriadoEm().isBefore(limite))
                .toList();
    }

    public AvisoComunitario publicar(AvisoComunitario aviso) {
        if (aviso == null || vazio(aviso.getTitulo()) || vazio(aviso.getMensagem())) {
            throw new IllegalArgumentException("Título e mensagem são obrigatórios.");
        }
        if (aviso.getLocalidadeId() == null) {
            throw new IllegalArgumentException("Informe a localidade do aviso.");
        }
        aviso.setTitulo(aviso.getTitulo().trim());
        aviso.setMensagem(aviso.getMensagem().trim());
        aviso.setCriadoEm(Instant.now());
        aviso.setDataExpiracao(aviso.getCriadoEm().plus(30, ChronoUnit.DAYS));
        return avisoRepository.salvar(aviso);
    }

    public MonitorComunitario monitorLocalidade(Integer localidadeId) {
        List<AvisoComunitario> avisos = listar(localidadeId);
        List<Reservatorio> participantes = reservatorioRepository.listarPorLocalidade(localidadeId).stream()
                .filter(Reservatorio::isConsentimentoAgregado)
                .toList();

        MonitorComunitario monitor = new MonitorComunitario();
        monitor.setIdMonitor(localidadeId);
        monitor.setLocalidadeId(localidadeId);
        monitor.setTotalAvisos(avisos.size());
        monitor.setAtualizadoEm(Instant.now());
        boolean suficiente = participantes.size() >= MINIMO_PARTICIPANTES_AGREGADO;
        monitor.setSuficiente(suficiente);
        monitor.setDadosInsuficientes(!suficiente);
        monitor.setParticipantes(suficiente ? Integer.toString(participantes.size()) : "menos de 5");

        // RN15: quantidades e condições sanitárias individuais não são expostas antes do mínimo agregado.
        if (suficiente) {
            monitor.setQuantidadeReservatorios(participantes.size());
            monitor.setReservatoriosCriticos(participantes.stream()
                    .filter(item -> item.getNivelPercentual() != null && item.getNivelPercentual() < 25)
                    .count());
            monitor.setReservatoriosEmAtencao(participantes.stream()
                    .filter(item -> item.getNivelPercentual() != null
                            && item.getNivelPercentual() >= 25 && item.getNivelPercentual() < 50)
                    .count());
            monitor.setRiscosSanitarios(participantes.stream()
                    .filter(item -> item.getChecklists() != null)
                    .filter(item -> item.getChecklists().stream().anyMatch(check -> !check.isConforme()))
                    .count());
            monitor.setUltimaSincronizacao(participantes.stream()
                    .map(Reservatorio::getUltimaSincronizacao)
                    .filter(Objects::nonNull)
                    .max(Instant::compareTo)
                    .orElse(null));
        }
        return monitor;
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
