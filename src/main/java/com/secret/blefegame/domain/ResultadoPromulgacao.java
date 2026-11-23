package com.secret.blefegame.domain;

import java.util.Objects;

public record ResultadoPromulgacao(Faccao promulgada, Faccao descartada) {
    public ResultadoPromulgacao {
        Objects.requireNonNull(promulgada, "nao pode ser nula a carta");
        Objects.requireNonNull(descartada, "nao pode ser nula a carta");
    }
}
