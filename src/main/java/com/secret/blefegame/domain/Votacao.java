package com.secret.blefegame.domain;

import java.util.*;

public class Votacao {

    private final Set<UUID> votantesEsperados;
    private final Map<UUID, Voto> votosRegistrados;

    Votacao(Set<UUID> jogadoresVivosIds) {
        Objects.requireNonNull(jogadoresVivosIds, "ID nao pode ser nulo ou vazio");
        if (jogadoresVivosIds.isEmpty()) {
            throw new IllegalArgumentException("ID está vazio");
        }
        this.votantesEsperados = Set.copyOf(jogadoresVivosIds);
        this.votosRegistrados = new HashMap<>();
    }

    public void registrar(UUID jogadorId, Voto voto) {
        Objects.requireNonNull(jogadorId, "ID não pode ser nulo");
        Objects.requireNonNull(voto, "voto tem que ser dado");
        if (!this.votantesEsperados.contains(jogadorId)) {
            throw new IllegalStateException("Jogador não pode votar");
        }
        if (this.votosRegistrados.putIfAbsent(jogadorId, voto) != null) {
            throw new IllegalStateException("Jogador não pode votar duas vezes");
        }
    }

    public boolean completa() {
        return this.votosRegistrados.size() == this.votantesEsperados.size();
    }

    public boolean aprovada() {
        if (!completa()) {
            throw new IllegalStateException("nem todo mundo votou ainda");
        }
        long votosSim = this.votosRegistrados.values().stream()
                .filter(v -> v == Voto.SIM)
                .count();
        long quantidadeVotosNao = this.votosRegistrados.size() - votosSim;
        return votosSim > quantidadeVotosNao;
    }

    public Map<UUID, Voto> votosRevelados() {
        if (!completa()) {
            throw new IllegalStateException("nem todo mundo votou ainda");
        }
        return Map.copyOf(this.votosRegistrados);
    }

    public int totalVotosComputados() {
        return this.votosRegistrados.size();
    }
}
