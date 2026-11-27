package com.secret.blefegame.infra.auth;

import java.security.Principal;
import java.util.Objects;
import java.util.UUID;

public record StompPrincipal(UUID jogadorId) implements Principal {

    @Override
    public String getName() {
        return this.jogadorId.toString();
    }
}
