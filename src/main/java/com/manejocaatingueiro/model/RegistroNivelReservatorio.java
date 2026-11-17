package com.manejocaatingueiro.model;

import java.time.Instant;
import java.time.LocalDate;

public class RegistroNivelReservatorio {
    private LocalDate data;
    private Double nivelPercentual;
    private String nivelQualitativo;
    private Double profundidadeLencolM;
    private Instant registradoEm;

    public RegistroNivelReservatorio() {
    }

    public RegistroNivelReservatorio(LocalDate data, Double nivelPercentual, String nivelQualitativo,
                                     Double profundidadeLencolM, Instant registradoEm) {
        this.data = data;
        this.nivelPercentual = nivelPercentual;
        this.nivelQualitativo = nivelQualitativo;
        this.profundidadeLencolM = profundidadeLencolM;
        this.registradoEm = registradoEm;
    }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public Double getNivelPercentual() { return nivelPercentual; }
    public void setNivelPercentual(Double nivelPercentual) { this.nivelPercentual = nivelPercentual; }
    public String getNivelQualitativo() { return nivelQualitativo; }
    public void setNivelQualitativo(String nivelQualitativo) { this.nivelQualitativo = nivelQualitativo; }
    public Double getProfundidadeLencolM() { return profundidadeLencolM; }
    public void setProfundidadeLencolM(Double profundidadeLencolM) { this.profundidadeLencolM = profundidadeLencolM; }
    public Instant getRegistradoEm() { return registradoEm; }
    public void setRegistradoEm(Instant registradoEm) { this.registradoEm = registradoEm; }
}
