package com.secret.blefegame.domain.evento;

import java.util.Objects;
import java.util.UUID;

public record NovoPresidente(UUID presidenteId) implements EventoPublico {

    public NovoPresidente {
        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");
    }
}
