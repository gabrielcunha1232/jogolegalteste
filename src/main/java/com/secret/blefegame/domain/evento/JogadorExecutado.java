package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;
import java.util.UUID;

public record JogadorExecutado(UUID presidenteId, UUID alvoId) implements EventoPublico {

    public JogadorExecutado {
        Objects.requireNonNull(presidenteId, "destinatario id nao pode ser nulo");
        Objects.requireNonNull(alvoId, "alvo idnao pode ser nulo");

        if (presidenteId.equals(alvoId)) {
            throw new AcaoInvalidaException("destinatario e alvo nao podem ter o mesmo id");
        }
    }
}
