package com.manejocaatingueiro.model;

import java.time.Instant;

/** Visão consolidada da climatologia e das observações usada pelo painel e pelo RF19. */
public class SerieHistoricaClimatica {
    private Integer localidadeId;
    private Double mediaHistorica;
    private Double chuvaAcumulada;
    private Double chuvaEsperada;
    private Integer anosBase;
    private boolean dadosSuficientes;
    private String fonte;
    private Instant dataAtualizacao;

    public Integer getLocalidadeId() { return localidadeId; }
    public void setLocalidadeId(Integer localidadeId) { this.localidadeId = localidadeId; }
    public Double getMediaHistorica() { return mediaHistorica; }
    public void setMediaHistorica(Double mediaHistorica) { this.mediaHistorica = mediaHistorica; }
    public Double getChuvaAcumulada() { return chuvaAcumulada; }
    public void setChuvaAcumulada(Double chuvaAcumulada) { this.chuvaAcumulada = chuvaAcumulada; }
    public Double getChuvaEsperada() { return chuvaEsperada; }
    public void setChuvaEsperada(Double chuvaEsperada) { this.chuvaEsperada = chuvaEsperada; }
    public Integer getAnosBase() { return anosBase; }
    public void setAnosBase(Integer anosBase) { this.anosBase = anosBase; }
    public boolean isDadosSuficientes() { return dadosSuficientes; }
    public void setDadosSuficientes(boolean dadosSuficientes) { this.dadosSuficientes = dadosSuficientes; }
    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
    public Instant getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(Instant dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
