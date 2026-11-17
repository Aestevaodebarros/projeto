package com.manejocaatingueiro.model;

public class PerfilProdutivo {
    private int numeroPessoas;
    private int bovinos;
    private int caprinosOvinos;
    private int aves;
    private int areaIrrigadaM2;

    public PerfilProdutivo() {
    }

    public PerfilProdutivo(int numeroPessoas, int bovinos, int caprinosOvinos, int aves, int areaIrrigadaM2) {
        this.numeroPessoas = numeroPessoas;
        this.bovinos = bovinos;
        this.caprinosOvinos = caprinosOvinos;
        this.aves = aves;
        this.areaIrrigadaM2 = areaIrrigadaM2;
    }

    public int getNumeroPessoas() { return numeroPessoas; }
    public void setNumeroPessoas(int numeroPessoas) { this.numeroPessoas = numeroPessoas; }
    public int getBovinos() { return bovinos; }
    public void setBovinos(int bovinos) { this.bovinos = bovinos; }
    public int getCaprinosOvinos() { return caprinosOvinos; }
    public void setCaprinosOvinos(int caprinosOvinos) { this.caprinosOvinos = caprinosOvinos; }
    public int getAves() { return aves; }
    public void setAves(int aves) { this.aves = aves; }
    public int getAreaIrrigadaM2() { return areaIrrigadaM2; }
    public void setAreaIrrigadaM2(int areaIrrigadaM2) { this.areaIrrigadaM2 = areaIrrigadaM2; }
}
