package com.manejocaatingueiro.model;

import java.time.Instant;

public class AvisoComunitario {
    private Long id;
    private Integer localidadeId;
    private String titulo;
    private String mensagem;
    private String autor;
    private Instant criadoEm;

    public AvisoComunitario() {
        this.criadoEm = Instant.now();
    }

    public AvisoComunitario(Long id, Integer localidadeId, String titulo, String mensagem, String autor) {
        this.id = id;
        this.localidadeId = localidadeId;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.autor = autor;
        this.criadoEm = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getLocalidadeId() { return localidadeId; }
    public void setLocalidadeId(Integer localidadeId) { this.localidadeId = localidadeId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public Instant getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }
}
