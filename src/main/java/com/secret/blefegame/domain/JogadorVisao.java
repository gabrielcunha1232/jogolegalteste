package com.secret.blefegame.domain;

import java.util.Objects;
import java.util.UUID;

public record JogadorVisao(UUID id, String nome, boolean vivo, boolean jaVotou) {

    public JogadorVisao {
        Objects.requireNonNull(id, "id nao pode ser nulo");
        Objects.requireNonNull(nome, "nome nao pode ser nulo");
    }
}