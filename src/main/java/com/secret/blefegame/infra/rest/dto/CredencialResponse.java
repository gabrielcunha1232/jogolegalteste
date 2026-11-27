package com.secret.blefegame.infra.rest.dto;

import java.util.Objects;
import java.util.UUID;

public record CredencialResponse(String codigoSala, UUID jogadorId, String token) {

    public CredencialResponse {
        Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
        Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
        Objects.requireNonNull(token, "token nao pode ser nulo");
    }
}