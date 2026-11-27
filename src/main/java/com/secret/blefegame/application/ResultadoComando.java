package com.secret.blefegame.application;

import com.secret.blefegame.domain.evento.EventoDominio;
import com.secret.blefegame.projector.dto.EstadoJogadorDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// O que o handler devolve depois de executar um comando.
// Tudo aqui é uma FOTO imutável tirada dentro do lock: o publisher usa isso depois de o lock ser solto,
// sem nunca encostar na Partida (que é mutável e não é thread-safe).
public record ResultadoComando(String codigoSala, List<EventoDominio> eventos, Map<UUID, EstadoJogadorDTO> estados) {

    public ResultadoComando {
        // codigoSala, eventos e estados não podem ser nulos

        // guarde cópias imutáveis (List.copyOf, Map.copyOf) para ninguém alterar depois
    }
}