package com.secret.blefegame.domain;

import java.util.Objects;
import java.util.UUID;

public record Governo(UUID presidenteId, UUID chancelerId){

    public Governo {
        Objects.requireNonNull(presidenteId, "ID do jogador está nulo");

        if (chancelerId != null && chancelerId.equals(presidenteId)) {
            throw new IllegalArgumentException("O presidente nao pode ser nomeado chanceler");
        }
    }

    public static Governo iniciarComPresidente(UUID presidenteId) {
        return new Governo(presidenteId, null);
    }

    public Governo comChanceler(UUID chancelerId) {
        Objects.requireNonNull(chancelerId, "chanceler ID nao pode ser nulo");
        return new Governo(this.presidenteId, chancelerId);
    }

    public boolean temChancelerNomeado() {
        return chancelerId != null;
    }

}
