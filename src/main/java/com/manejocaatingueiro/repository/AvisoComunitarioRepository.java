package com.manejocaatingueiro.repository;

import com.manejocaatingueiro.model.AvisoComunitario;

import java.util.List;

public interface AvisoComunitarioRepository {
    List<AvisoComunitario> listarPorLocalidade(Integer localidadeId);
    AvisoComunitario salvar(AvisoComunitario aviso);
}
