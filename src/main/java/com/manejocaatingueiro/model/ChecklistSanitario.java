package com.manejocaatingueiro.model;

import java.time.Instant;

/** Item do checklist sanitário associado a um reservatório. */
public class ChecklistSanitario {
    private Long id;
    private String item;
    private boolean conforme;
    private String resultado;
    private String observacoes;
    private Instant dataAvaliacao = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public boolean isConforme() { return conforme; }
    public void setConforme(boolean conforme) {
        this.conforme = conforme;
        this.resultado = conforme ? "CONFORME" : "NAO_CONFORME";
    }
    public String getResultado() { return resultado == null ? (conforme ? "CONFORME" : "NAO_CONFORME") : resultado; }
    public void setResultado(String resultado) {
        this.resultado = resultado;
        if (resultado != null) this.conforme = "CONFORME".equalsIgnoreCase(resultado);
    }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Instant getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(Instant dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
    /** Alias de compatibilidade com os documentos JSON já persistidos. */
    public Instant getData() { return dataAvaliacao; }
    public void setData(Instant data) { this.dataAvaliacao = data; }
}
