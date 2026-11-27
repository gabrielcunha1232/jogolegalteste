package com.secret.blefegame.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record VisaoPartida(
        String codigoSala,
        FaseDoJogo fase,
        UUID anfitriaoId,
        List<JogadorVisao> jogadores,
        UUID presidenteId,
        UUID chancelerId,
        int liberaisPromulgadas,
        int fascistasPromulgadas,
        int eleicoesFracassadas,
        PoderExecutivo poderPendente,
        boolean vetoDisponivel,
        // --- só deste jogador ---
        Faccao minhaFaccao,
        boolean souHitler,
        Set<UUID> aliadosVisiveis,
        List<Faccao> minhasCartas) {

    public VisaoPartida {
        Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
        Objects.requireNonNull(fase, "fase esta nula");
        Objects.requireNonNull(jogadores, "jogadores estao nulo");
        Objects.requireNonNull(aliadosVisiveis, "aliados estao nulos");
        Objects.requireNonNull(minhasCartas, "cartas estao nulas");

        jogadores = List.copyOf(jogadores);
        aliadosVisiveis = Set.copyOf(aliadosVisiveis);
        minhasCartas = List.copyOf(minhasCartas);
    }
}