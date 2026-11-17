package com.manejocaatingueiro.model;

public class FaseCultura {
    private String nome;
    private Double kc;
    private Integer duracaoDias;

    public FaseCultura() {
    }

    public FaseCultura(String nome, Double kc, Integer duracaoDias) {
        this.nome = nome;
        this.kc = kc;
        this.duracaoDias = duracaoDias;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Double getKc() { return kc; }
    public void setKc(Double kc) { this.kc = kc; }
    public Integer getDuracaoDias() { return duracaoDias; }
    public void setDuracaoDias(Integer duracaoDias) { this.duracaoDias = duracaoDias; }
}
