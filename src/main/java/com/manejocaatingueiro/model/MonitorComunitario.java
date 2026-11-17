package com.manejocaatingueiro.model;

import java.time.Instant;

/** Agregado pseudonimizado do estado dos reservatórios de uma localidade (RN15). */
public class MonitorComunitario {
    private Integer idMonitor;
    private Integer localidadeId;
    private Integer quantidadeReservatorios;
    private String participantes;
    private long reservatoriosCriticos;
    private long reservatoriosEmAtencao;
    private long riscosSanitarios;
    private int totalAvisos;
    private boolean suficiente;
    private boolean dadosInsuficientes;
    private Instant ultimaSincronizacao;
    private Instant atualizadoEm = Instant.now();

    public Integer getIdMonitor() { return idMonitor; }
    public void setIdMonitor(Integer idMonitor) { this.idMonitor = idMonitor; }
    public Integer getLocalidadeId() { return localidadeId; }
    public void setLocalidadeId(Integer localidadeId) { this.localidadeId = localidadeId; }
    public Integer getQuantidadeReservatorios() { return quantidadeReservatorios; }
    public void setQuantidadeReservatorios(Integer quantidadeReservatorios) { this.quantidadeReservatorios = quantidadeReservatorios; }
    public String getParticipantes() { return participantes; }
    public void setParticipantes(String participantes) { this.participantes = participantes; }
    public long getReservatoriosCriticos() { return reservatoriosCriticos; }
    public void setReservatoriosCriticos(long reservatoriosCriticos) { this.reservatoriosCriticos = reservatoriosCriticos; }
    public long getReservatoriosEmAtencao() { return reservatoriosEmAtencao; }
    public void setReservatoriosEmAtencao(long reservatoriosEmAtencao) { this.reservatoriosEmAtencao = reservatoriosEmAtencao; }
    public long getRiscosSanitarios() { return riscosSanitarios; }
    public void setRiscosSanitarios(long riscosSanitarios) { this.riscosSanitarios = riscosSanitarios; }
    /** Alias singular para preservar o nome do campo usado pela resposta da API anterior. */
    public long getRiscoSanitario() { return riscosSanitarios; }
    public int getTotalAvisos() { return totalAvisos; }
    public void setTotalAvisos(int totalAvisos) { this.totalAvisos = totalAvisos; }
    public boolean isSuficiente() { return suficiente; }
    public void setSuficiente(boolean suficiente) { this.suficiente = suficiente; }
    public boolean isDadosInsuficientes() { return dadosInsuficientes; }
    public void setDadosInsuficientes(boolean dadosInsuficientes) { this.dadosInsuficientes = dadosInsuficientes; }
    public Instant getUltimaSincronizacao() { return ultimaSincronizacao; }
    public void setUltimaSincronizacao(Instant ultimaSincronizacao) { this.ultimaSincronizacao = ultimaSincronizacao; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(Instant atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
