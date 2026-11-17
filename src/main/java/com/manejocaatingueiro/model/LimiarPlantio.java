package com.manejocaatingueiro.model;

public class LimiarPlantio {
    private String id;
    private Integer culturaId;
    private Long usuarioId;
    private Double limiarMm;

    public LimiarPlantio() {
    }

    public LimiarPlantio(Integer culturaId, Long usuarioId, Double limiarMm) {
        this.id = culturaId + "_" + usuarioId;
        this.culturaId = culturaId;
        this.usuarioId = usuarioId;
        this.limiarMm = limiarMm;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getCulturaId() { return culturaId; }
    public void setCulturaId(Integer culturaId) { this.culturaId = culturaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Double getLimiarMm() { return limiarMm; }
    public void setLimiarMm(Double limiarMm) { this.limiarMm = limiarMm; }
}
