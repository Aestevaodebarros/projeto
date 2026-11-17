package com.manejocaatingueiro.model;

import java.time.Instant;
import java.time.LocalDate;

/** Nome usado pela persistência e pela API para a classe conceitual RegistroNivel. */
public class RegistroNivelReservatorio extends RegistroNivel {
    public RegistroNivelReservatorio() { super(); }
    public RegistroNivelReservatorio(LocalDate data, Double nivelPercentual, String nivelQualitativo,
                                     Double profundidadeLencolM, Instant registradoEm) {
        super(data, nivelPercentual, nivelQualitativo, profundidadeLencolM, registradoEm);
    }
    public RegistroNivelReservatorio(Long id, LocalDate data, Double nivelPercentual, String nivelQualitativo,
                                     Double profundidadeLencolM, Instant registradoEm) {
        super(id, data, nivelPercentual, nivelQualitativo, profundidadeLencolM, registradoEm);
    }
}
