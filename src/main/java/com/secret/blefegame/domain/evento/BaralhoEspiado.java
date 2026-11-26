package com.secret.blefegame.domain.evento;

import java.util.Objects;
import java.util.UUID;

public record BaralhoEspiado(UUID presidenteId) implements EventoPublico {

    public BaralhoEspiado {
        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");
    }
}
