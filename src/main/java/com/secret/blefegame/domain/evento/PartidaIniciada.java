package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record PartidaIniciada(int totalJogadores, UUID primeiroPresidenteId, List<UUID> ordemDosJogadores) implements EventoPublico {

    public PartidaIniciada {
        Objects.requireNonNull(primeiroPresidenteId, "id do presidente nao pode ser nulo");
        Objects.requireNonNull(ordemDosJogadores, "a ordem de jogadores não pode ser nula");
        if (totalJogadores < 5 || totalJogadores > 10) {
            throw new AcaoInvalidaException("total de jogadores deve ser entre cinco e dez");
        }

        if (!ordemDosJogadores.contains(primeiroPresidenteId)) {
            throw new AcaoInvalidaException("o ID do primeiro presidente nao esta presente na ordem dos jogadores");
        }

        ordemDosJogadores = List.copyOf(ordemDosJogadores);
    }
}
