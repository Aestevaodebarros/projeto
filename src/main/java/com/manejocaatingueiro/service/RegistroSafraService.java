package com.manejocaatingueiro.service;

import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.model.Cultura;
import com.manejocaatingueiro.model.RegistroSafra;
import com.manejocaatingueiro.model.StatusSafra;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.repository.PlantioRepository;
import com.manejocaatingueiro.repository.RegistroSafraRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
public class RegistroSafraService {
    private final RegistroSafraRepository safraRepository;
    private final PlantioRepository plantioRepository;
    private final PlantioService plantioService;
    private final AlertaService alertaService;
    private final UsuarioService usuarioService;

    public RegistroSafraService(RegistroSafraRepository safraRepository, PlantioRepository plantioRepository,
                                PlantioService plantioService, AlertaService alertaService,
                                UsuarioService usuarioService) {
        this.safraRepository = safraRepository;
        this.plantioRepository = plantioRepository;
        this.plantioService = plantioService;
        this.alertaService = alertaService;
        this.usuarioService = usuarioService;
    }

    public RegistroSafra iniciar(Usuario usuario, Integer culturaId, LocalDate dataPlantio, String observacao) {
        Cultura cultura = plantioRepository.buscarCultura(culturaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cultura não encontrada."));
        if (dataPlantio == null || dataPlantio.isAfter(LocalDate.now(ZoneId.of("America/Sao_Paulo")))) {
            throw new IllegalArgumentException("Informe uma data de plantio válida, hoje ou anterior.");
        }
        RegistroSafra safra = new RegistroSafra();
        safra.setUsuarioId(usuario.getId());
        safra.setLocalidadeId(usuario.getLocalidadeId());
        safra.setCulturaId(cultura.getId());
        safra.setCulturaNome(cultura.getNome());
        safra.setDataPlantio(dataPlantio);
        safra.setObservacao(observacao);
        return safraRepository.salvar(safra);
    }

    public List<RegistroSafra> listar(Long usuarioId) {
        return safraRepository.listarPorUsuario(usuarioId);
    }

    public RegistroSafra registrarResultado(Long usuarioId, Long safraId,
                                             StatusSafra resultado, String observacao) {
        RegistroSafra safra = buscarDoUsuario(usuarioId, safraId);
        if (resultado == null || resultado == StatusSafra.EM_ANDAMENTO) {
            throw new IllegalArgumentException("Informe o resultado final da safra.");
        }
        safra.setResultado(resultado);
        safra.setDataColheita(LocalDate.now(ZoneId.of("America/Sao_Paulo")));
        if (observacao != null && !observacao.isBlank()) {
            safra.setObservacao(observacao.trim());
        }
        safra.setAtualizadoEm(Instant.now());
        return safraRepository.salvar(safra);
    }

    public Map<String, Object> avaliarRisco(Long usuarioId, Long safraId) {
        RegistroSafra safra = buscarDoUsuario(usuarioId, safraId);
        if (safra.getResultado() != StatusSafra.EM_ANDAMENTO) {
            throw new IllegalArgumentException("A avaliação de risco só se aplica a uma safra em andamento.");
        }
        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        Map<String, Object> avaliacao = plantioService.avaliarAptidao(
                usuario.getLocalidadeId(), usuario, safra.getDataPlantio());
        Object culturas = avaliacao.get("culturas");
        Map<?, ?> culturaAvaliada = culturas instanceof List<?> lista ? lista.stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .filter(item -> safra.getCulturaId().equals(item.get("culturaId")))
                .findFirst().orElse(null) : null;
        if (culturaAvaliada == null) {
            throw new RecursoNaoEncontradoException("Cultura da safra não encontrada.");
        }
        Object indice = culturaAvaliada.get("indiceViabilidade");
        boolean alertaEmitido = false;
        if (indice instanceof Number valor && valor.doubleValue() < 0.7) {
            alertaService.alertarRiscoSafra(safra.getLocalidadeId(), safra.getCulturaId(),
                    "Risco de déficit para " + safra.getCulturaNome()
                            + ". Considere reduzir irrigação, priorizar água para pessoas e animais e conversar com a associação.");
            alertaEmitido = true;
        }
        return Map.of(
                "safraId", safraId,
                "cultura", safra.getCulturaNome(),
                "dadosInsuficientes", Boolean.TRUE.equals(culturaAvaliada.get("dadosInsuficientes")),
                "indiceViabilidade", indice == null ? 0.0 : indice,
                "alertaEmitido", alertaEmitido,
                "avaliacao", culturaAvaliada
        );
    }

    private RegistroSafra buscarDoUsuario(Long usuarioId, Long safraId) {
        return safraRepository.buscarPorId(safraId)
                .filter(registro -> usuarioId.equals(registro.getUsuarioId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Safra não encontrada."));
    }
}
