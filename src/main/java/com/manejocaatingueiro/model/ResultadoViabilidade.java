package com.manejocaatingueiro.model;

import java.time.Instant;
import java.time.LocalDate;

/** Avaliação de oferta hídrica versus demanda de uma cultura (RF19/RN18). */
public class ResultadoViabilidade {
    private Long idResultado;
    private Integer culturaId;
    private String culturaNome;
    private Double ofertaHidricaMm;
    private Double demandaCulturaMm;
    private Double indiceViabilidade;
    private String classificacao;
    private boolean dadosInsuficientes;
    private Double chuvaAcumuladaMm;
    private Double chuvaEsperadaMm;
    private Double aguaReservadaMm;
    private LocalDate dataPlantio;
    private String fonteClimatologica;
    private String fonteAgronomica;
    private Instant dataCalculo = Instant.now();

    public Long getIdResultado() { return idResultado; }
    public void setIdResultado(Long idResultado) { this.idResultado = idResultado; }
    public Integer getCulturaId() { return culturaId; }
    public void setCulturaId(Integer culturaId) { this.culturaId = culturaId; }
    public String getCulturaNome() { return culturaNome; }
    public void setCulturaNome(String culturaNome) { this.culturaNome = culturaNome; }
    public Double getOfertaHidricaMm() { return ofertaHidricaMm; }
    public void setOfertaHidricaMm(Double ofertaHidricaMm) { this.ofertaHidricaMm = ofertaHidricaMm; }
    public Double getDemandaCulturaMm() { return demandaCulturaMm; }
    public void setDemandaCulturaMm(Double demandaCulturaMm) { this.demandaCulturaMm = demandaCulturaMm; }
    public Double getIndiceViabilidade() { return indiceViabilidade; }
    public void setIndiceViabilidade(Double indiceViabilidade) { this.indiceViabilidade = indiceViabilidade; }
    public String getClassificacao() { return classificacao; }
    public void setClassificacao(String classificacao) { this.classificacao = classificacao; }
    public boolean isDadosInsuficientes() { return dadosInsuficientes; }
    public void setDadosInsuficientes(boolean dadosInsuficientes) { this.dadosInsuficientes = dadosInsuficientes; }
    public Double getChuvaAcumuladaMm() { return chuvaAcumuladaMm; }
    public void setChuvaAcumuladaMm(Double chuvaAcumuladaMm) { this.chuvaAcumuladaMm = chuvaAcumuladaMm; }
    public Double getChuvaEsperadaMm() { return chuvaEsperadaMm; }
    public void setChuvaEsperadaMm(Double chuvaEsperadaMm) { this.chuvaEsperadaMm = chuvaEsperadaMm; }
    public Double getAguaReservadaMm() { return aguaReservadaMm; }
    public void setAguaReservadaMm(Double aguaReservadaMm) { this.aguaReservadaMm = aguaReservadaMm; }
    public LocalDate getDataPlantio() { return dataPlantio; }
    public void setDataPlantio(LocalDate dataPlantio) { this.dataPlantio = dataPlantio; }
    public String getFonteClimatologica() { return fonteClimatologica; }
    public void setFonteClimatologica(String fonteClimatologica) { this.fonteClimatologica = fonteClimatologica; }
    public String getFonteAgronomica() { return fonteAgronomica; }
    public void setFonteAgronomica(String fonteAgronomica) { this.fonteAgronomica = fonteAgronomica; }
    public Instant getDataCalculo() { return dataCalculo; }
    public void setDataCalculo(Instant dataCalculo) { this.dataCalculo = dataCalculo; }
}
