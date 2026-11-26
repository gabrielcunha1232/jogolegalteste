package com.secret.blefegame.domain.evento;

import java.util.Objects;
import java.util.UUID;

public record VetoDecidido(UUID presidenteId, boolean aceito) implements EventoPublico {

    public VetoDecidido {
        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");
    }
}
