package com.manejocaatingueiro.model;

public class Localidade {
    private Integer id;
    private String nome;
    private String tipo;
    private Integer paiId;
    private String codigoIbge;
    private String uf;
    private Double latitude;
    private Double longitude;
    private String estacaoInmetCodigo;

    public Localidade() {
    }

    public Localidade(Integer id, String nome, String tipo, Integer paiId, String codigoIbge, String uf, Double latitude, Double longitude, String estacaoInmetCodigo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.paiId = paiId;
        this.codigoIbge = codigoIbge;
        this.uf = uf;
        this.latitude = latitude;
        this.longitude = longitude;
        this.estacaoInmetCodigo = estacaoInmetCodigo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getPaiId() { return paiId; }
    public void setPaiId(Integer paiId) { this.paiId = paiId; }
    public String getCodigoIbge() { return codigoIbge; }
    public void setCodigoIbge(String codigoIbge) { this.codigoIbge = codigoIbge; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getEstacaoInmetCodigo() { return estacaoInmetCodigo; }
    public void setEstacaoInmetCodigo(String estacaoInmetCodigo) { this.estacaoInmetCodigo = estacaoInmetCodigo; }
}
