package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.PoderExecutivo;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;
import java.util.UUID;

public record PoderExecutivoLiberado(PoderExecutivo poder, UUID presidenteId) implements EventoPublico {

    public PoderExecutivoLiberado {
        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");
        Objects.requireNonNull(poder, "poder nao pode ser nulo");

        if (poder == PoderExecutivo.NENHUM) {
            throw new AcaoInvalidaException("poder executivo nao pode ser nenhum.");
        }
    }
}
