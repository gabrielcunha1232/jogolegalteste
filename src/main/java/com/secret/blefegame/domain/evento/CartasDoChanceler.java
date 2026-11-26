package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.Faccao;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CartasDoChanceler(UUID destinatarioId, List<Faccao> cartas) implements EventoPrivado {

    public CartasDoChanceler {
        Objects.requireNonNull(destinatarioId, "id nao pode estar nulo");
        Objects.requireNonNull(cartas, "cartas nao podem estar nulas");

        if (cartas.size() != 2) {
            throw new IllegalArgumentException("devem existir exatamente 3 cartas na mao");
        }

        cartas = List.copyOf(cartas);
    }
}
