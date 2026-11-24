package com.secret.blefegame.domain;

import java.util.Objects;
import java.util.UUID;

public class Jogador {

    private final UUID id;
    private final String nome;
    private final Papel papel;
    private boolean vivo;

    Jogador(UUID id, String nome, Papel papel) {
        this.id = Objects.requireNonNull(id, "id nao pode ser nulo");
        this.nome = Objects.requireNonNull(nome, "nome nao pode ser nulo");
        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode estar vazio");
        }
        this.papel = Objects.requireNonNull(papel, "papel nao pode ser inexistente");
        this.vivo = true;
    }

    public UUID id() {
        return this.id;
    }

    public String nome() {
        return this.nome;
    }

    public boolean estaVivo() {
        return this.vivo;
    }

    void executar() {
        if (!this.vivo) {
            throw new IllegalStateException("jogador ja esta morto");
        }

        this.vivo = false;
    }

    Papel papel() {
        return papel;
    }

    @Override
    public String toString() {
        return "Jogador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", vivo=" + vivo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Jogador jogador = (Jogador) o;
        return Objects.equals(id, jogador.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
