package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.Faccao;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CartasEspiadas(UUID destinatarioId, List<Faccao> cartas) implements EventoPrivado {

    public CartasEspiadas {
        Objects.requireNonNull(destinatarioId, "destinatario id nao pode ser nulo");
        Objects.requireNonNull(cartas, "cartas nao pode ser nulo");

        if (cartas.size() != 3) {
            throw new AcaoInvalidaException("devem ter 3 cartas ao todo");
        }

        cartas = List.copyOf(cartas);
    }
}
