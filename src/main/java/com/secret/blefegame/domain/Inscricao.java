package com.secret.blefegame.domain;

import java.util.Objects;
import java.util.UUID;

public record Inscricao(UUID id, String nome) {

    public Inscricao(UUID id, String nome) {
        this.id = Objects.requireNonNull(id, "id nao pode ser nulo");
        this.nome = Objects.requireNonNull(nome, "nome nao pode ser nulo").strip();
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode estar vazio");
        }
    }
}
