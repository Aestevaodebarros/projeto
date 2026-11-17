package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.AvisoComunitario;
import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.repository.AvisoComunitarioRepository;
import com.manejocaatingueiro.repository.ReservatorioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComunidadService {
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
        return avisoRepository.salvar(aviso);
    }

    public Map<String, Object> monitorLocalidade(Integer localidadeId) {
        List<AvisoComunitario> avisos = listar(localidadeId);
        List<Reservatorio> participantes = reservatorioRepository.listarPorLocalidade(localidadeId).stream()
                .filter(Reservatorio::isConsentimentoAgregado)
                .toList();
        Map<String, Object> retorno = new HashMap<>();
        retorno.put("localidadeId", localidadeId);
        retorno.put("totalAvisos", avisos.size());
        retorno.put("atualizadoEm", Instant.now().toString());
        retorno.put("dadosInsuficientes", participantes.size() < 5);
        retorno.put("participantes", participantes.size() < 5 ? "menos de 5" : participantes.size());
        retorno.put("suficiente", participantes.size() >= 5);
        if (participantes.size() >= 5) {
            retorno.put("reservatoriosCriticos", participantes.stream()
                    .filter(item -> item.getNivelPercentual() != null && item.getNivelPercentual() < 25)
                    .count());
            retorno.put("reservatoriosEmAtencao", participantes.stream()
                    .filter(item -> item.getNivelPercentual() != null
                            && item.getNivelPercentual() >= 25 && item.getNivelPercentual() < 50)
                    .count());
            retorno.put("riscoSanitario", participantes.stream()
                    .filter(item -> item.getChecklists() != null)
                    .filter(item -> item.getChecklists().stream().anyMatch(check -> !check.isConforme()))
                    .count());
            retorno.put("ultimaSincronizacao", participantes.stream()
                    .map(Reservatorio::getUltimaSincronizacao)
                    .filter(java.util.Objects::nonNull)
                    .max(Instant::compareTo)
                    .orElse(null));
        }
        return retorno;
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
