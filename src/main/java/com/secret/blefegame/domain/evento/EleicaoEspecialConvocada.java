package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;
import java.util.UUID;

public record EleicaoEspecialConvocada(UUID presidenteQueConvocouId, UUID novoPresidenteId) implements EventoPublico {

    public EleicaoEspecialConvocada {
        Objects.requireNonNull(presidenteQueConvocouId, "presidente id nao pode ser nulo");
        Objects.requireNonNull(novoPresidenteId, "alvo id nao pode ser nulo");

        if (presidenteQueConvocouId.equals(novoPresidenteId)) {
            throw new AcaoInvalidaException("alvo nao pode ser o proprio presidente");
        }
    }
}
