package com.secret.blefegame.domain.evento;

import java.util.Objects;
import java.util.UUID;

public record EleicaoRejeitada(UUID proximoPresidenteId, int eleicoesFracassadasSeguidas) implements EventoPublico {

    public EleicaoRejeitada {
        Objects.requireNonNull(proximoPresidenteId, "id nao pode ser nulo");

        if (eleicoesFracassadasSeguidas <= 0) {
            throw new IllegalArgumentException("eleicoes fracassadas nao pode estar numa sequencia zerada");
        }
        // pense: quando chega a 3 (caos), este valor aparece como 3 ou já como 0? decida e documente
    }
}
