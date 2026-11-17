package com.manejocaatingueiro.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Reservatorio {
    private Long id;
    private String nome;
    private String tipo;
    private Integer localidadeId;
    private Double capacidadeLitros;
    private Double nivelPercentual;
    private String nivelQualitativo;
    private Double profundidadeLençolM;
    private Double diametroM;
    private Double profundidadeTotalM;
    private Long usuarioResponsavelId;
    private boolean consentimentoAgregado;
    private Instant ultimaSincronizacao;
    private List<ChecklistSanitario> checklists = new ArrayList<>();
    private List<RegistroNivelReservatorio> historicoNiveis = new ArrayList<>();

    public Reservatorio() {
        this.ultimaSincronizacao = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Integer getLocalidadeId() { return localidadeId; }
    public void setLocalidadeId(Integer localidadeId) { this.localidadeId = localidadeId; }
    public Double getCapacidadeLitros() { return capacidadeLitros; }
    public void setCapacidadeLitros(Double capacidadeLitros) { this.capacidadeLitros = capacidadeLitros; }
    public Double getNivelPercentual() { return nivelPercentual; }
    public void setNivelPercentual(Double nivelPercentual) { this.nivelPercentual = nivelPercentual; }
    public String getNivelQualitativo() { return nivelQualitativo; }
    public void setNivelQualitativo(String nivelQualitativo) { this.nivelQualitativo = nivelQualitativo; }
    public Double getProfundidadeLençolM() { return profundidadeLençolM; }
    public void setProfundidadeLençolM(Double profundidadeLençolM) { this.profundidadeLençolM = profundidadeLençolM; }
    public Double getDiametroM() { return diametroM; }
    public void setDiametroM(Double diametroM) { this.diametroM = diametroM; }
    public Double getProfundidadeTotalM() { return profundidadeTotalM; }
    public void setProfundidadeTotalM(Double profundidadeTotalM) { this.profundidadeTotalM = profundidadeTotalM; }
    public Long getUsuarioResponsavelId() { return usuarioResponsavelId; }
    public void setUsuarioResponsavelId(Long usuarioResponsavelId) { this.usuarioResponsavelId = usuarioResponsavelId; }
    public boolean isConsentimentoAgregado() { return consentimentoAgregado; }
    public void setConsentimentoAgregado(boolean consentimentoAgregado) { this.consentimentoAgregado = consentimentoAgregado; }
    public Instant getUltimaSincronizacao() { return ultimaSincronizacao; }
    public void setUltimaSincronizacao(Instant ultimaSincronizacao) { this.ultimaSincronizacao = ultimaSincronizacao; }
    public List<ChecklistSanitario> getChecklists() { return checklists; }
    public void setChecklists(List<ChecklistSanitario> checklists) { this.checklists = checklists; }
    public List<RegistroNivelReservatorio> getHistoricoNiveis() { return historicoNiveis; }
    public void setHistoricoNiveis(List<RegistroNivelReservatorio> historicoNiveis) { this.historicoNiveis = historicoNiveis; }

    public static class ChecklistSanitario {
        private Long id;
        private String item;
        private boolean conforme;
        private Instant data;

        public ChecklistSanitario() { this.data = Instant.now(); }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getItem() { return item; }
        public void setItem(String item) { this.item = item; }
        public boolean isConforme() { return conforme; }
        public void setConforme(boolean conforme) { this.conforme = conforme; }
        public Instant getData() { return data; }
        public void setData(Instant data) { this.data = data; }
    }
}
