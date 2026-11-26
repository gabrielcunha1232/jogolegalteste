package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;
import java.util.UUID;

public record JogadorInvestigado(UUID presidenteId, UUID alvoId) implements EventoPublico {

    public JogadorInvestigado {
        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");
        Objects.requireNonNull(alvoId, "alvo id nao pode ser nulo");

        if (presidenteId.equals(alvoId)) {
            throw new AcaoInvalidaException("alvo nao pode ser o proprio presidente");
        }
    }
}

