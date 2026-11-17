package com.manejocaatingueiro.model;

public class Cultura {
    private Integer id;
    private String nome;
    private Double demandaTotalMm;
    private Double limiarPadraoMm;
    private String fonte;
    private Integer cicloDias;

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
}
