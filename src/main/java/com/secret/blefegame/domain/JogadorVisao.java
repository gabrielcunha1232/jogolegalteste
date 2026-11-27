package com.secret.blefegame.domain;

import java.util.UUID;

// Um jogador como a mesa o vê. Sem papel, sem facção.
public record JogadorVisao(UUID id, String nome, boolean vivo, boolean jaVotou) {

    public JogadorVisao {
        // id e nome não podem ser nulos
    }
}