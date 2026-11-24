package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.Voto;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record VotosRevelados (Map<UUID, Voto> votos, boolean aprovada) implements EventoPublico {

    public VotosRevelados {
        Objects.requireNonNull(votos, "voto nao pode ser nulo");

        votos = Map.copyOf(votos);
    }
}
