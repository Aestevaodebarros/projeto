package com.manejocaatingueiro.model;

/** Parâmetro versionado de uma fase da cultura, com rastreabilidade da fonte (RNF11). */
public class ParametroAgronomico {
    private Integer idParametro;
    private Integer culturaId;
    private String fase;
    private Double kc;
    private Double eto;
    private Integer duracaoFase;
    private Double necessidadeHidricaMm;
    private String fonte;
    private String versao;

    public Integer getIdParametro() { return idParametro; }
    public void setIdParametro(Integer idParametro) { this.idParametro = idParametro; }
    public Integer getCulturaId() { return culturaId; }
    public void setCulturaId(Integer culturaId) { this.culturaId = culturaId; }
    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }
    public Double getKc() { return kc; }
    public void setKc(Double kc) { this.kc = kc; }
    public Double getEto() { return eto; }
    public void setEto(Double eto) { this.eto = eto; }
    public Integer getDuracaoFase() { return duracaoFase; }
    public void setDuracaoFase(Integer duracaoFase) { this.duracaoFase = duracaoFase; }
    public Double getNecessidadeHidricaMm() { return necessidadeHidricaMm; }
    public void setNecessidadeHidricaMm(Double necessidadeHidricaMm) { this.necessidadeHidricaMm = necessidadeHidricaMm; }
    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
    public String getVersao() { return versao; }
    public void setVersao(String versao) { this.versao = versao; }
}
