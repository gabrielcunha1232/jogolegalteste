package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;
import java.util.UUID;

public record JogadorEntrou(UUID jogadorId, String nome, int totalInscritos) implements EventoPublico {

    public JogadorEntrou{
        Objects.requireNonNull(jogadorId, "id do jogador nao pode ser nulo");
        Objects.requireNonNull(nome, "nome nao pode ser nulo");

        if (totalInscritos <= 0) {
            throw new AcaoInvalidaException("total de inscritos deve ser maior que zero");
        }
    }
}
