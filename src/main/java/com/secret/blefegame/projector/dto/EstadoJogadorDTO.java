package com.secret.blefegame.projector.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

// O estado completo da partida PARA UM jogador específico: parte pública da mesa + o que só ele pode saber.
// Usa tipos simples (String) para fase/facção/poder: o contrato com o cliente não deve depender de enums do domínio.
// Este DTO substitui o EstadoPublicoDTO do guia: a parte pública já está aqui, e o log da mesa vem pelos eventos.
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
        // meuId, codigoSala, fase, jogadores, aliadosVisiveis e minhasCartas não podem ser nulos

        // guarde cópias imutáveis das listas e do conjunto

        // minhaFaccao pode ser nula (no lobby ainda não há papéis); fora do lobby, não deveria ser
    }
}