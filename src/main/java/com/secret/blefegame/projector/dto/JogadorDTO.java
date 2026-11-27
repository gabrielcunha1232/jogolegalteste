package com.secret.blefegame.projector.dto;

import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;
import java.util.UUID;

public record JogadorDTO(UUID id, String nome, boolean vivo, boolean ehPresidente, boolean ehChanceler, boolean jaVotou) {

    public JogadorDTO {
        Objects.requireNonNull(id, "id nao pode ser nulo");
        Objects.requireNonNull(nome, "nome nao pode ser nulo");

        if (nome.isBlank()) {
            throw new AcaoInvalidaException("nome nao pode estar em branco");
        }
    }
}