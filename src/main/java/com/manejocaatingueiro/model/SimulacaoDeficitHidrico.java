package com.manejocaatingueiro.model;

import java.time.Instant;

/** Resultado de simulação hipotética de mudança na oferta/consumo hídrico (RF20). */
public class SimulacaoDeficitHidrico {
    private Long idSimulacao;
    private Long usuarioId;
    private Double variacaoConsumo;
    private Double variacaoOferta;
    private Double autonomiaSimulada;
    private String resultado;
    private Instant dataSimulacao = Instant.now();

    public Long getIdSimulacao() { return idSimulacao; }
    public void setIdSimulacao(Long idSimulacao) { this.idSimulacao = idSimulacao; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Double getVariacaoConsumo() { return variacaoConsumo; }
    public void setVariacaoConsumo(Double variacaoConsumo) { this.variacaoConsumo = variacaoConsumo; }
    public Double getVariacaoOferta() { return variacaoOferta; }
    public void setVariacaoOferta(Double variacaoOferta) { this.variacaoOferta = variacaoOferta; }
    public Double getAutonomiaSimulada() { return autonomiaSimulada; }
    public void setAutonomiaSimulada(Double autonomiaSimulada) { this.autonomiaSimulada = autonomiaSimulada; }
    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }
    public Instant getDataSimulacao() { return dataSimulacao; }
    public void setDataSimulacao(Instant dataSimulacao) { this.dataSimulacao = dataSimulacao; }
}
