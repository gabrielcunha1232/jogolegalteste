package com.secret.blefegame.domain.evento;

import java.util.Objects;
import java.util.UUID;

public record VotoRegistrado(UUID jogadorId, int totalVotosComputados, int votantesEsperados) implements EventoPublico {

    public VotoRegistrado {
        Objects.requireNonNull(jogadorId, "id do jogador nao pode ser nulo");

        if (totalVotosComputados < 1 || totalVotosComputados > votantesEsperados) {
            throw new IllegalStateException("os votos computados estao fora do alcance esperado");
        }
    }
}
