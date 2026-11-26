package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.Faccao;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CartasDoPresidente(UUID destinatarioId, List<Faccao> cartas) implements EventoPrivado {

    public CartasDoPresidente {
        Objects.requireNonNull(destinatarioId, "id nao pode estar nulo");
        Objects.requireNonNull(cartas, "cartas nao podem estar nulas");

        if (cartas.size() != 3) {
            throw new IllegalArgumentException("devem existir exatamente 3 cartas na mao");
        }

        cartas = List.copyOf(cartas);
    }
}
