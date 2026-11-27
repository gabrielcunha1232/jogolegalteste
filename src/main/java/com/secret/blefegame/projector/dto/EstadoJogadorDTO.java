package com.secret.blefegame.projector.dto;


import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record EstadoJogadorDTO(
        UUID meuId,
        String codigoSala,
        String fase,
        UUID anfitriaoId,
        List<JogadorDTO> jogadores,
        UUID presidenteId,
        UUID chancelerId,
        int liberaisPromulgadas,
        int fascistasPromulgadas,
        int eleicoesFracassadas,
        String poderPendente,
        boolean vetoDisponivel,
        // --- a partir daqui, só o próprio jogador vê ---
        String minhaFaccao,
        boolean souHitler,
        Set<UUID> aliadosVisiveis,
        List<String> minhasCartas) {

    public EstadoJogadorDTO {
        Objects.requireNonNull(meuId, "meu id nao pode ser nulo");
        Objects.requireNonNull(codigoSala, "codigo sala nao pode ser nulo");
        Objects.requireNonNull(fase, "fase nao pode ser nula");
        Objects.requireNonNull(jogadores, "jogadores nao pode estar nulo");
        Objects.requireNonNull(aliadosVisiveis, "aliados nao podem estar nulo");
        Objects.requireNonNull(minhasCartas, "minhas cartas nao podem estar nulas");

        jogadores = List.copyOf(jogadores);
        aliadosVisiveis = Set.copyOf(aliadosVisiveis);
        minhasCartas = List.copyOf(minhasCartas);

        if (!fase.equals("AGUARDANDO_JOGADORES")) {
            Objects.requireNonNull(minhaFaccao, "faccao nao pode ser nula quando o jogo ja iniciou");
        }
    }
}