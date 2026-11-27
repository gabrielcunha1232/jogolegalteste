package com.secret.blefegame.infra.auth;

import java.util.Objects;
import java.util.UUID;

public record Credencial(String codigoSala, UUID jogadorId) {

    public Credencial {
        Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
        Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
    }
}