package com.secret.blefegame.domain.excecao;

public class AcaoInvalidaException extends RuntimeException{

    public AcaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
