package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.Faccao;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;
import java.util.UUID;

public record LealdadeInvestigada(UUID destinatarioId, UUID alvoId, Faccao faccao) implements EventoPrivado {

    public LealdadeInvestigada {
        Objects.requireNonNull(destinatarioId, "id nao pode estar nulo");
        Objects.requireNonNull(alvoId, "alvo id nao pode ser nulo");
        Objects.requireNonNull(faccao, "faccao nao pode ser nula");

        if (destinatarioId.equals(alvoId)) {
            throw new AcaoInvalidaException("destinatario e alvo nao podem ter o mesmo id");
        }
    }
}
