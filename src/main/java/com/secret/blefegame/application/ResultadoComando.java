package com.secret.blefegame.application;

import com.secret.blefegame.domain.evento.EventoDominio;
import com.secret.blefegame.projector.dto.EstadoJogadorDTO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record ResultadoComando(String codigoSala, List<EventoDominio> eventos, Map<UUID, EstadoJogadorDTO> estados) {

    public ResultadoComando {
        Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
        Objects.requireNonNull(eventos, "os eventos nao podem estar nulos");
        Objects.requireNonNull(estados, "estado nao pode ser nulo");

        eventos = List.copyOf(eventos);
        estados = Map.copyOf(estados);
    }
}