package com.manejocaatingueiro.model;

import java.time.Instant;

public class SolicitacaoAgua {
    private Long id;
    private Integer localidadeId;
    private Long usuarioId;
    private Long reservatorioId;
    private String motivo;
    private StatusSolicitacaoAgua status = StatusSolicitacaoAgua.PENDENTE;
    private Instant criadaEm = Instant.now();
    private Instant atualizadaEm = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getLocalidadeId() { return localidadeId; }
    public void setLocalidadeId(Integer localidadeId) { this.localidadeId = localidadeId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getReservatorioId() { return reservatorioId; }
    public void setReservatorioId(Long reservatorioId) { this.reservatorioId = reservatorioId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public StatusSolicitacaoAgua getStatus() { return status; }
    public void setStatus(StatusSolicitacaoAgua status) { this.status = status; }
    public Instant getCriadaEm() { return criadaEm; }
    public void setCriadaEm(Instant criadaEm) { this.criadaEm = criadaEm; }
    public Instant getAtualizadaEm() { return atualizadaEm; }
    public void setAtualizadaEm(Instant atualizadaEm) { this.atualizadaEm = atualizadaEm; }
}
