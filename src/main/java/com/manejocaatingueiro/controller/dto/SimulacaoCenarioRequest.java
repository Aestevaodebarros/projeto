package com.manejocaatingueiro.controller.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record SimulacaoCenarioRequest(
        @DecimalMin("0.0") @DecimalMax("2.0") Double fatorChuva,
        @DecimalMin("0.0") @DecimalMax("1000.0") Integer acrescimoPessoas,
        @DecimalMin("0.0") @DecimalMax("10000.0") Integer acrescimoBovinos,
        @DecimalMin("0.0") @DecimalMax("10000.0") Integer acrescimoCaprinosOvinos,
        @DecimalMin("0.0") @DecimalMax("100000.0") Integer acrescimoAves,
        @DecimalMin("0.0") @DecimalMax("10000.0") Integer acrescimoCavalosJumentos,
        @DecimalMin("0.0") @DecimalMax("10000.0") Integer acrescimoSuinos
) {
}
