package com.manejocaatingueiro.model;

import java.time.Instant;

/** Recomendação consultiva, nunca prescritiva, derivada do resultado de viabilidade (RN12). */
public class SugestaoPlantio {
    private Long idSugestao;
    private Long usuarioId;
    private Integer culturaId;
    private Long resultadoViabilidadeId;
    private String recomendacao;
    private String grauConfianca;
    private String justificativa;
    private String fonte;
    private Instant dataGeracao = Instant.now();

    public Long getIdSugestao() { return idSugestao; }
    public void setIdSugestao(Long idSugestao) { this.idSugestao = idSugestao; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Integer getCulturaId() { return culturaId; }
    public void setCulturaId(Integer culturaId) { this.culturaId = culturaId; }
    public Long getResultadoViabilidadeId() { return resultadoViabilidadeId; }
    public void setResultadoViabilidadeId(Long resultadoViabilidadeId) { this.resultadoViabilidadeId = resultadoViabilidadeId; }
    public String getRecomendacao() { return recomendacao; }
    public void setRecomendacao(String recomendacao) { this.recomendacao = recomendacao; }
    public String getGrauConfianca() { return grauConfianca; }
    public void setGrauConfianca(String grauConfianca) { this.grauConfianca = grauConfianca; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
    public Instant getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(Instant dataGeracao) { this.dataGeracao = dataGeracao; }
}
