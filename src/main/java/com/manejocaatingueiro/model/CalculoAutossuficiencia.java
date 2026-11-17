package com.manejocaatingueiro.model;

import java.time.Instant;

/** Resultado consolidado do cálculo de autonomia hídrica da propriedade (RF19/RN17). */
public class CalculoAutossuficiencia {
    private Long idCalculo;
    private Long usuarioId;
    private Integer localidadeId;
    private double reservaUtilLitros;
    private double consumoDiarioLitros;
    private double entradasContinuasLitrosDia;
    private double consumoLiquidoDiarioLitros;
    private Double autonomiaDias;
    private String statusHidrico;
    private String fonteParametrosConsumo;
    private String versaoParametrosConsumo;
    private Instant dataCalculo = Instant.now();

    public Long getIdCalculo() { return idCalculo; }
    public void setIdCalculo(Long idCalculo) { this.idCalculo = idCalculo; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Integer getLocalidadeId() { return localidadeId; }
    public void setLocalidadeId(Integer localidadeId) { this.localidadeId = localidadeId; }
    public double getReservaUtilLitros() { return reservaUtilLitros; }
    public void setReservaUtilLitros(double reservaUtilLitros) { this.reservaUtilLitros = reservaUtilLitros; }
    public double getConsumoDiarioLitros() { return consumoDiarioLitros; }
    public void setConsumoDiarioLitros(double consumoDiarioLitros) { this.consumoDiarioLitros = consumoDiarioLitros; }
    public double getEntradasContinuasLitrosDia() { return entradasContinuasLitrosDia; }
    public void setEntradasContinuasLitrosDia(double entradasContinuasLitrosDia) { this.entradasContinuasLitrosDia = entradasContinuasLitrosDia; }
    public double getConsumoLiquidoDiarioLitros() { return consumoLiquidoDiarioLitros; }
    public void setConsumoLiquidoDiarioLitros(double consumoLiquidoDiarioLitros) { this.consumoLiquidoDiarioLitros = consumoLiquidoDiarioLitros; }
    public Double getAutonomiaDias() { return autonomiaDias; }
    public void setAutonomiaDias(Double autonomiaDias) { this.autonomiaDias = autonomiaDias; }
    public String getStatusHidrico() { return statusHidrico; }
    public void setStatusHidrico(String statusHidrico) { this.statusHidrico = statusHidrico; }
    public String getFonteParametrosConsumo() { return fonteParametrosConsumo; }
    public void setFonteParametrosConsumo(String fonteParametrosConsumo) { this.fonteParametrosConsumo = fonteParametrosConsumo; }
    public String getVersaoParametrosConsumo() { return versaoParametrosConsumo; }
    public void setVersaoParametrosConsumo(String versaoParametrosConsumo) { this.versaoParametrosConsumo = versaoParametrosConsumo; }
    public Instant getDataCalculo() { return dataCalculo; }
    public void setDataCalculo(Instant dataCalculo) { this.dataCalculo = dataCalculo; }
}
