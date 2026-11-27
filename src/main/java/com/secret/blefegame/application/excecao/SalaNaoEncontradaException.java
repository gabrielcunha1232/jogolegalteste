package com.secret.blefegame.application.excecao;

// Sala que não existe: erro de quem pediu, não bug do sistema. A infra traduz para a resposta certa
// (404 no REST, mensagem em /user/queue/erros no STOMP).
public class SalaNaoEncontradaException extends RuntimeException {

    public SalaNaoEncontradaException(String codigoSala) {
        // chamar super com uma mensagem que inclua o código da sala
    }
}