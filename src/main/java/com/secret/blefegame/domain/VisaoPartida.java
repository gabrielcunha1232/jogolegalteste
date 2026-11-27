package com.secret.blefegame.domain;

import java.util.List;
import java.util.Set;
import java.util.UUID;

// Tudo que UM jogador pode saber sobre a partida agora. Montada pela própria Partida (visaoPara),
// então o segredo não sai do domínio: aqui só entra o que esse jogador tem direito de ver.
// Imutável. Pública, porque o projector (outro pacote) precisa lê-la.
public record VisaoPartida(
        String codigoSala,
        FaseDoJogo fase,
        UUID anfitriaoId,
        List<JogadorVisao> jogadores,
        UUID presidenteId,           // nulo antes de iniciar
        UUID chancelerId,            // nulo enquanto não houver nomeação
        int liberaisPromulgadas,
        int fascistasPromulgadas,
        int eleicoesFracassadas,
        PoderExecutivo poderPendente,
        boolean vetoDisponivel,
        // --- só deste jogador ---
        Faccao minhaFaccao,          // nula no lobby
        boolean souHitler,
        Set<UUID> aliadosVisiveis,
        List<Faccao> minhasCartas) {

    public VisaoPartida {
        // codigoSala, fase, jogadores, aliadosVisiveis e minhasCartas não podem ser nulos

        // guarde cópias imutáveis das listas e do conjunto
    }
}