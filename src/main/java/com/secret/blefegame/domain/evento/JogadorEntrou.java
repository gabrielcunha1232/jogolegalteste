package com.secret.blefegame.domain.evento;

import java.util.Objects;
import java.util.UUID;

public record JogadorEntrou(UUID jogadorId, String nome, int totalInscritos) implements EventoPublico {

    public JogadorEntrou{
        Objects.requireNonNull(jogadorId, "id do jogador nao pode ser nulo");
        Objects.requireNonNull(nome, "nome nao pode ser nulo");

        if (totalInscritos <= 0) {
            throw new IllegalArgumentException("total de inscritos deve ser maior que zero");
        }
    }
}
