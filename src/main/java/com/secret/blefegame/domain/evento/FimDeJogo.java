package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.Faccao;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;

public record FimDeJogo(Faccao vencedora, MotivoVitoria motivo) implements EventoPublico{

    public FimDeJogo {
        Objects.requireNonNull(vencedora, "faccao nao pode estar nula");
        Objects.requireNonNull(motivo, "motivo nao pode ser nulo");

        boolean coerente = switch (vencedora) {
            case LIBERAL -> motivo == MotivoVitoria.POLITICAS_LIBERAIS || motivo == MotivoVitoria.HITLER_EXECUTADO;
            case FASCISTA -> motivo == MotivoVitoria.POLITICAS_FASCISTAS || motivo == MotivoVitoria.HITLER_ELEITO;
        };

        if (!coerente) {
            throw new IllegalArgumentException(
                    "Motivo " + motivo + " é incoerente com a facção vencedora " + vencedora
            );
        }
    }
}
