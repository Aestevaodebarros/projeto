package com.manejocaatingueiro.controller;

import com.manejocaatingueiro.controller.dto.ApiErrorResponse;
import com.manejocaatingueiro.domain.exception.ConflitoDeCadastroException;
import com.manejocaatingueiro.domain.exception.CredenciaisInvalidasException;
import com.manejocaatingueiro.domain.exception.RecursoNaoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> tratarNaoEncontrado(RecursoNaoEncontradoException exception) {
        return resposta(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ApiErrorResponse> tratarCredenciaisInvalidas(CredenciaisInvalidasException exception) {
        return resposta(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(ConflitoDeCadastroException.class)
    public ResponseEntity<ApiErrorResponse> tratarConflito(ConflitoDeCadastroException exception) {
        return resposta(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiErrorResponse> tratarRequisicaoInvalida(Exception exception) {
        String mensagem = exception instanceof MethodArgumentNotValidException validationException
                ? validationException.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(error -> error.getField() + ": valor inválido.")
                    .orElse("A requisição contém dados inválidos.")
                : exception instanceof HttpMessageNotReadableException
                    ? "O corpo da requisição está ausente ou contém JSON inválido."
                    : exception.getMessage();
        return resposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> tratarErroInterno(Exception exception) {
        logger.error("Erro não tratado durante o processamento da requisição", exception);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno ao processar a requisição.");
    }

    private ResponseEntity<ApiErrorResponse> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(Instant.now(), status.value(), mensagem));
    }
}
