package com.secret.blefegame.domain;

import java.util.*;

public class Votacao {

    private final Set<UUID> votantesEsperados;
    private final Map<UUID, Voto> votosRegistrados;

    Votacao(Set<UUID> jogadoresVivosIds) {
        // 1. Valide que jogadoresVivosIds não é nulo nem vazio
        Objects.requireNonNull(jogadoresVivosIds, "ID nao pode ser nulo ou vazio");
        if (jogadoresVivosIds.isEmpty()) {
            throw new IllegalArgumentException("ID está vazio");
        }
        // 2. Guarde uma cópia imutável em votantesEsperados (Set.copyOf)
        this.votantesEsperados = Set.copyOf(jogadoresVivosIds);
        // 3. Inicialize votosRegistrados como HashMap vazio
        this.votosRegistrados = new HashMap<>();
    }

    public void registrar(UUID jogadorId, Voto voto) {
        // 1. Valide que jogadorId e voto não são nulos
        Objects.requireNonNull(jogadorId, "ID não pode ser nulo");
        Objects.requireNonNull(voto, "voto tem que ser dado");
        // 2. Verifique se jogadorId está contido em votantesEsperados (senão, lance exceção: morto ou fora da partida não vota)
        if (!this.votantesEsperados.contains(jogadorId)) {
            throw new IllegalStateException("Jogador não pode votar");
        }
        // 3. Verifique se jogadorId JÁ existe em votosRegistrados (invariante: cada jogador vota uma vez só)
        // 4. Insira o voto no mapa votosRegistrados
        if (this.votosRegistrados.putIfAbsent(jogadorId, voto) != null) {
            throw new IllegalStateException("Jogador não pode votar duas vezes");
        }
    }

    public boolean completa() {
        // 1. Retorne true se votosRegistrados.size() == votantesEsperados.size()
        return this.votosRegistrados.size() == this.votantesEsperados.size();
    }

    public boolean aprovada() {
        // 1. Valide que completa() é verdadeiro (não se calcula resultado parcial)
        if (!completa()) {
            throw new IllegalStateException("nem todo mundo votou ainda");
        }
        // 2. Conte quantos votos são Voto.SIM e quantos são Voto.NEIN
        long votosSim = this.votosRegistrados.values().stream()
                .filter(v -> v == Voto.SIM)
                .count();
        long quantidadeVotosNao = this.votosRegistrados.size() - votosSim;
        // 3. Retorne true estritamente se votos SIM > votos NEIN (empate rejeita)
        return votosSim > quantidadeVotosNao;
    }

    public Map<UUID, Voto> votosRevelados() {
        // 1. INVARIANTE CRÍTICA: Verifique se completa() é verdadeiro; se não for, lance IllegalStateException
        if (!completa()) {
            throw new IllegalStateException("nem todo mundo votou ainda");
        }
        // 2. Retorne uma cópia imutável do mapa votosRegistrados (Map.copyOf)
        return Map.copyOf(this.votosRegistrados);
    }

    public int totalVotosComputados() {
        // 1. Retorne votosRegistrados.size() (permite mostrar progresso "3/7 votaram" sem revelar o conteúdo)
        return this.votosRegistrados.size();
    }
}
