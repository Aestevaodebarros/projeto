package com.manejocaatingueiro.service;

import com.manejocaatingueiro.model.AvisoComunitario;
import com.manejocaatingueiro.repository.AvisoComunitarioRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComunidadService {
    private final AvisoComunitarioRepository avisoRepository;

    public ComunidadService(AvisoComunitarioRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
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
        Map<String, Object> retorno = new HashMap<>();
        retorno.put("localidadeId", localidadeId);
        retorno.put("totalAvisos", avisos.size());
        retorno.put("atualizadoEm", Instant.now().toString());
        retorno.put("participantes", 12);
        retorno.put("suficiente", true);
        return retorno;
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
