package com.manejocaatingueiro.model;

import java.util.ArrayList;
import java.util.List;

public class Cultura {
    private Integer id;
    private String nome;
    private Double demandaTotalMm;
    private Double limiarPadraoMm;
    private String fonte;
    private Integer cicloDias;
    private Double etoReferenciaMmDia = 6.3;
    private String versaoParametros = "v1";
    private List<FaseCultura> fases = new ArrayList<>();

    public Cultura() {
    }

    public Cultura(Integer id, String nome, Double demandaTotalMm, Double limiarPadraoMm, String fonte, Integer cicloDias) {
        this.id = id;
        this.nome = nome;
        this.demandaTotalMm = demandaTotalMm;
        this.limiarPadraoMm = limiarPadraoMm;
        this.fonte = fonte;
        this.cicloDias = cicloDias;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Double getDemandaTotalMm() { return demandaTotalMm; }
    public void setDemandaTotalMm(Double demandaTotalMm) { this.demandaTotalMm = demandaTotalMm; }
    public Double getLimiarPadraoMm() { return limiarPadraoMm; }
    public void setLimiarPadraoMm(Double limiarPadraoMm) { this.limiarPadraoMm = limiarPadraoMm; }
    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
    public Integer getCicloDias() { return cicloDias; }
    public void setCicloDias(Integer cicloDias) { this.cicloDias = cicloDias; }
    public Double getEtoReferenciaMmDia() { return etoReferenciaMmDia; }
    public void setEtoReferenciaMmDia(Double etoReferenciaMmDia) { this.etoReferenciaMmDia = etoReferenciaMmDia; }
    public String getVersaoParametros() { return versaoParametros; }
    public void setVersaoParametros(String versaoParametros) { this.versaoParametros = versaoParametros; }
    public List<FaseCultura> getFases() { return fases; }
    public void setFases(List<FaseCultura> fases) { this.fases = fases; }
}
