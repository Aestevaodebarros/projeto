package com.manejocaatingueiro.service;

import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import com.manejocaatingueiro.model.Reservatorio;
import com.manejocaatingueiro.model.PerfilUsuario;
import com.manejocaatingueiro.model.SolicitacaoAgua;
import com.manejocaatingueiro.model.StatusSolicitacaoAgua;
import com.manejocaatingueiro.model.Usuario;
import com.manejocaatingueiro.repository.ReservatorioRepository;
import com.manejocaatingueiro.repository.SolicitacaoAguaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

@Service
public class SolicitacaoAguaService {
    private final SolicitacaoAguaRepository solicitacaoRepository;
    private final ReservatorioRepository reservatorioRepository;
    private final boolean habilitada;

    public SolicitacaoAguaService(SolicitacaoAguaRepository solicitacaoRepository,
                                  ReservatorioRepository reservatorioRepository,
                                  @Value("${solicitacao-agua.ativo:false}") boolean habilitada) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.reservatorioRepository = reservatorioRepository;
        this.habilitada = habilitada;
    }

    public SolicitacaoAgua abrir(Usuario usuario, Long reservatorioId, String motivo) {
        if (!habilitada) {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED,
                    "Canal de apoio hídrico depende do gate e de pactuação institucional (RN14).");
        }
        if (usuario == null || reservatorioId == null || motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Reservatório e motivo são obrigatórios.");
        }
        Reservatorio reservatorio = reservatorioRepository.buscarPorId(reservatorioId)
                .filter(item -> usuario.getId().equals(item.getUsuarioResponsavelId()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Reservatório não encontrado."));
        if (reservatorio.getNivelPercentual() == null || reservatorio.getNivelPercentual() > 25) {
            throw new IllegalArgumentException("Solicitação permitida somente com nível do reservatório igual ou inferior a 25%.");
        }

        SolicitacaoAgua solicitacao = new SolicitacaoAgua();
        solicitacao.setUsuarioId(usuario.getId());
        solicitacao.setLocalidadeId(usuario.getLocalidadeId());
        solicitacao.setReservatorioId(reservatorioId);
        solicitacao.setMotivo(motivo.trim());
        return solicitacaoRepository.salvar(solicitacao);
    }

    public List<SolicitacaoAgua> listar(Usuario usuario, StatusSolicitacaoAgua status, boolean consultarLocalidade) {
        List<SolicitacaoAgua> lista = consultarLocalidade
                ? solicitacaoRepository.listarPorLocalidade(usuario.getLocalidadeId())
                : solicitacaoRepository.listarPorUsuario(usuario.getId());
        return lista.stream()
                .filter(item -> status == null || item.getStatus() == status)
                .toList();
    }

    public SolicitacaoAgua atualizarStatus(Long id, Usuario responsavel, StatusSolicitacaoAgua novoStatus) {
        SolicitacaoAgua solicitacao = solicitacaoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitação não encontrada."));
        if (responsavel == null || responsavel.getLocalidadeId() == null
                || !responsavel.getLocalidadeId().equals(solicitacao.getLocalidadeId())) {
            throw new RecursoNaoEncontradoException("Solicitação não encontrada.");
        }
        PerfilUsuario perfil = responsavel.getPerfil();
        if (perfil != PerfilUsuario.ADMIN && perfil != PerfilUsuario.GESTOR_PUBLICO
                && perfil != PerfilUsuario.LIDER_COMUNITARIO) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Somente liderança comunitária ou gestão pública pode atualizar solicitações.");
        }
        if (novoStatus == null || !transicaoPermitida(solicitacao.getStatus(), novoStatus)) {
            throw new IllegalArgumentException("Transição de status não permitida.");
        }
        solicitacao.setStatus(novoStatus);
        solicitacao.setAtualizadaEm(Instant.now());
        return solicitacaoRepository.salvar(solicitacao);
    }

    private boolean transicaoPermitida(StatusSolicitacaoAgua atual, StatusSolicitacaoAgua proximo) {
        return switch (atual) {
            case PENDENTE -> proximo == StatusSolicitacaoAgua.AGENDADA
                    || proximo == StatusSolicitacaoAgua.CANCELADA;
            case AGENDADA -> proximo == StatusSolicitacaoAgua.ATENDIDA
                    || proximo == StatusSolicitacaoAgua.CANCELADA;
            case ATENDIDA, CANCELADA -> false;
        };
    }
}
