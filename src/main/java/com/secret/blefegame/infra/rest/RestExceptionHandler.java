package com.secret.blefegame.infra.rest;

import com.secret.blefegame.application.excecao.SalaNaoEncontradaException;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;
import com.secret.blefegame.infra.dto.ErroDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(SalaNaoEncontradaException.class)
    public ResponseEntity<ErroDTO> salaNaoEncontrada(SalaNaoEncontradaException e) {
        ErroDTO erro = new ErroDTO("SALA_NAO_ENCONTRADA", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(AcaoInvalidaException.class)
    public ResponseEntity<ErroDTO> acaoInvalida(AcaoInvalidaException e) {
        ErroDTO erroDTO = new ErroDTO("ACAO_INVALIDA", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erroDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroDTO> requisicaoInvalida(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining(", "));

        if (mensagem.isBlank()) {
            mensagem = "Dados inválidos na requisição";
        }

        ErroDTO erroDTO = new ErroDTO("REQUISICAO_INVALIDA", mensagem);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroDTO> erroInterno(Exception e) {
        log.error("Erro inesperado no servidor", e);
        ErroDTO erroDTO = new ErroDTO("ERRO_INTERNO", "Erro interno no servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erroDTO);
    }

}