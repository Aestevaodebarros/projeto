package com.manejocaatingueiro.model;

import java.time.Instant;

public class ClimatologiaMensal {
    private Integer localidadeId;
    private Integer mes;
    private Double precipitacaoMediaMm;
    private Integer anosBase;
    private String fonte;
    private Instant atualizadoEm;

    public ClimatologiaMensal() {
    }

    public Integer getLocalidadeId() { return localidadeId; }
    public void setLocalidadeId(Integer localidadeId) { this.localidadeId = localidadeId; }
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    public Double getPrecipitacaoMediaMm() { return precipitacaoMediaMm; }
    public void setPrecipitacaoMediaMm(Double precipitacaoMediaMm) { this.precipitacaoMediaMm = precipitacaoMediaMm; }
    public Integer getAnosBase() { return anosBase; }
    public void setAnosBase(Integer anosBase) { this.anosBase = anosBase; }
    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(Instant atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
