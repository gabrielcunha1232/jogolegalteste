package com.secret.blefegame.infra.dto;

import java.util.Objects;

public record ErroDTO(String codigo, String mensagem) {

    public ErroDTO {
        Objects.requireNonNull(codigo, "codigo nao pode ser nulo");
        Objects.requireNonNull(mensagem, "mensagem nao pode ser nula");
    }
}