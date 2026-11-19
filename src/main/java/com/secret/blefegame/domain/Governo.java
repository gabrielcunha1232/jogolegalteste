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

    public static Governo iniciarComPresidente(UUID presidenteID) {
        return new Governo(presidenteID, null);
    }

    public Governo comChanceler(UUID chancelerID) {
        Objects.requireNonNull(chancelerID, "chanceler ID nao pode ser nulo");
        return new Governo(this.presidenteId, chancelerID);
    }

    public boolean temChancelerNomeado() {
        return chancelerId != null;
    }

}
