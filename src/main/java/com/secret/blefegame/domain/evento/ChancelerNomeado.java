package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;
import java.util.UUID;

public record ChancelerNomeado(UUID presidenteId, UUID candidatoId) implements EventoPublico {

    public ChancelerNomeado {
        Objects.requireNonNull(presidenteId, "id do presidente nao pode ser nulo");
        Objects.requireNonNull(candidatoId, "id do candidato nao pode ser nulo");

        if (presidenteId.equals(candidatoId)) {
            throw new AcaoInvalidaException("os ids nao podem ser iguais");
        }
    }
}
