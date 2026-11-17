package com.manejocaatingueiro.model;

import java.time.Instant;

public class ParametrosConsumo {
    private Double bovinosLitrosDia = 51.5;
    private Double caprinosOvinosLitrosDia = 6.0;
    private Double avesLitrosDia = 0.35;
    private Double equinosLitrosDia = 41.0;
    private Double suinosLitrosDia = 6.0;
    private Double irrigacaoLitrosM2Dia = 4.0;
    private String fonte = "Anexo RNF14: CleytonSF_TESE; ThiagoMS_MONO; faixas ajustáveis";
    private String versao = "v1";
    private Instant atualizadoEm = Instant.now();

    public Double getBovinosLitrosDia() { return bovinosLitrosDia; }
    public void setBovinosLitrosDia(Double bovinosLitrosDia) { this.bovinosLitrosDia = bovinosLitrosDia; }
    public Double getCaprinosOvinosLitrosDia() { return caprinosOvinosLitrosDia; }
    public void setCaprinosOvinosLitrosDia(Double caprinosOvinosLitrosDia) { this.caprinosOvinosLitrosDia = caprinosOvinosLitrosDia; }
    public Double getAvesLitrosDia() { return avesLitrosDia; }
    public void setAvesLitrosDia(Double avesLitrosDia) { this.avesLitrosDia = avesLitrosDia; }
    public Double getEquinosLitrosDia() { return equinosLitrosDia; }
    public void setEquinosLitrosDia(Double equinosLitrosDia) { this.equinosLitrosDia = equinosLitrosDia; }
    public Double getSuinosLitrosDia() { return suinosLitrosDia; }
    public void setSuinosLitrosDia(Double suinosLitrosDia) { this.suinosLitrosDia = suinosLitrosDia; }
    public Double getIrrigacaoLitrosM2Dia() { return irrigacaoLitrosM2Dia; }
    public void setIrrigacaoLitrosM2Dia(Double irrigacaoLitrosM2Dia) { this.irrigacaoLitrosM2Dia = irrigacaoLitrosM2Dia; }
    public String getFonte() { return fonte; }
    public void setFonte(String fonte) { this.fonte = fonte; }
    public String getVersao() { return versao; }
    public void setVersao(String versao) { this.versao = versao; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(Instant atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
