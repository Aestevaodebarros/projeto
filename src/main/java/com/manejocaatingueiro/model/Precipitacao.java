package com.manejocaatingueiro.model;

import java.time.Instant;

public class Precipitacao {
    private Long id;
    private Integer localidadeId;
    private Double volumeMm;
    private String fonte;
    private Instant data;

    public Precipitacao() {
    }

    public Precipitacao(Long id, Integer localidadeId, Double volumeMm, String fonte, Instant data) {
        this.id = id;
        this.localidadeId = localidadeId;
        this.volumeMm = volumeMm;
        this.fonte = fonte;
        this.data = data;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getLocalidadeId() { return localidadeId; }
    public void setLocalidadeId(Integer localidadeId) { this.localidadeId = localidadeId; }
    public Double getVolumeMm() { return volumeMm; }
    public void setVolumeMm(Double volumeMm) { this.volumeMm = volumeMm; }
    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
    public Instant getData() { return data; }
    public void setData(Instant data) { this.data = data; }
}
