package com.secret.blefegame.domain.evento;

import java.util.Objects;
import java.util.UUID;

public record GovernoEleito(UUID presidenteId, UUID chancelerId) implements EventoPublico {

    public GovernoEleito {
        Objects.requireNonNull(presidenteId, "ID do jogador está nulo");

        if (chancelerId.equals(presidenteId)) {
            throw new IllegalArgumentException("O presidente nao pode ser nomeado chanceler");
        }
    }
}
