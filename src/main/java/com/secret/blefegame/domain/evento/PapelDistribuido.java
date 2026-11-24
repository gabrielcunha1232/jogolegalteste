package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.Faccao;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record PapelDistribuido(UUID destinatarioId, Faccao faccao, boolean ehHitler, List<UUID> aliadosVisiveis) implements EventoPrivado {

    public PapelDistribuido{
        Objects.requireNonNull(destinatarioId, "id do destinatario nao pode ser nulo");
        Objects.requireNonNull(faccao, "a faccao de jogadores não pode ser nula");
        Objects.requireNonNull(aliadosVisiveis, "lista de aliados nao pode ser nula");

        if (ehHitler && faccao != Faccao.FASCISTA) {
            throw new AcaoInvalidaException("faccao deve ser coincidida com o partido do jogador");
        }

        aliadosVisiveis = List.copyOf(aliadosVisiveis);
    }
}
