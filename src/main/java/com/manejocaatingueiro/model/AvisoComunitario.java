package com.manejocaatingueiro.model;

/** Nome de compatibilidade da API para a classe conceitual AvisoMural. */
public class AvisoComunitario extends AvisoMural {
    public AvisoComunitario() { super(); }
    public AvisoComunitario(Long id, Integer localidadeId, String titulo, String mensagem, String autor) {
        super(id, localidadeId, titulo, mensagem, autor);
    }
}
