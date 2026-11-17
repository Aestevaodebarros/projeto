package com.manejocaatingueiro.model;

import java.time.Instant;
import java.time.LocalDate;

public class RegistroSafra {
    private Long id;
    private Long usuarioId;
    private Integer localidadeId;
    private Integer culturaId;
    private String culturaNome;
    private LocalDate dataPlantio;
    private LocalDate dataColheita;
    private StatusSafra resultado = StatusSafra.EM_ANDAMENTO;
    private String observacao;
    private Instant criadoEm = Instant.now();
    private Instant atualizadoEm = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Integer getLocalidadeId() { return localidadeId; }
    public void setLocalidadeId(Integer localidadeId) { this.localidadeId = localidadeId; }
    public Integer getCulturaId() { return culturaId; }
    public void setCulturaId(Integer culturaId) { this.culturaId = culturaId; }
    public String getCulturaNome() { return culturaNome; }
    public void setCulturaNome(String culturaNome) { this.culturaNome = culturaNome; }
    public LocalDate getDataPlantio() { return dataPlantio; }
    public void setDataPlantio(LocalDate dataPlantio) { this.dataPlantio = dataPlantio; }
    public LocalDate getDataColheita() { return dataColheita; }
    public void setDataColheita(LocalDate dataColheita) { this.dataColheita = dataColheita; }
    public StatusSafra getResultado() { return resultado; }
    public void setResultado(StatusSafra resultado) { this.resultado = resultado; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public Instant getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(Instant atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
