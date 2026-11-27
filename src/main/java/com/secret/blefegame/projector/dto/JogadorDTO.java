package com.secret.blefegame.projector.dto;

import java.util.UUID;

// Um jogador como a MESA o enxerga. Nada de papel/facção aqui, nunca.
public record JogadorDTO(UUID id, String nome, boolean vivo, boolean ehPresidente, boolean ehChanceler, boolean jaVotou) {

    public JogadorDTO {
        // id e nome não podem ser nulos
    }
}