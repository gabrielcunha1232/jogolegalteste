package com.secret.blefegame.domain.evento;

import java.util.Objects;
import java.util.UUID;

public record VetoProposto(UUID chancelerId) implements EventoPublico {

    public VetoProposto {
        Objects.requireNonNull(chancelerId, "chanceler ID nao pode ser nulo");
    }
}
