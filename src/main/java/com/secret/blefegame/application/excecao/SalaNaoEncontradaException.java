package com.secret.blefegame.application.excecao;

public class SalaNaoEncontradaException extends RuntimeException {

    public SalaNaoEncontradaException(String codigoSala) {
        super(codigoSala);
    }
}