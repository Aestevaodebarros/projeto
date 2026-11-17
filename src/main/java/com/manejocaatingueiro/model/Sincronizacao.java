package com.manejocaatingueiro.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Estado e fila persistente de operações locais pendentes de sincronização externa (RF17). */
public class Sincronizacao {
    private Long idSincronizacao;
    private Long usuarioId;
    private String status = "SEM_PENDENCIAS";
    private Instant dataUltimaSincronizacao;
    private Instant atualizadoEm = Instant.now();
    private int quantidadePendencias;
    private List<PendenciaSincronizacao> pendencias = new ArrayList<>();

    public Long getIdSincronizacao() { return idSincronizacao; }
    public void setIdSincronizacao(Long idSincronizacao) { this.idSincronizacao = idSincronizacao; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getDataUltimaSincronizacao() { return dataUltimaSincronizacao; }
    public void setDataUltimaSincronizacao(Instant dataUltimaSincronizacao) { this.dataUltimaSincronizacao = dataUltimaSincronizacao; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(Instant atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    public List<PendenciaSincronizacao> getPendencias() {
        if (pendencias == null) pendencias = new ArrayList<>();
        return pendencias;
    }
    public void setPendencias(List<PendenciaSincronizacao> pendencias) { this.pendencias = pendencias == null ? new ArrayList<>() : pendencias; }
    public int getQuantidadePendencias() { return getPendencias().size(); }
    public void setQuantidadePendencias(int quantidadePendencias) { this.quantidadePendencias = Math.max(0, quantidadePendencias); }
}
