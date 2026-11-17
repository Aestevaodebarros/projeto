package com.manejocaatingueiro.domain.exception;

public class ConflitoDeCadastroException extends RuntimeException {
    public ConflitoDeCadastroException(String mensagem) {
        super(mensagem);
    }
}
