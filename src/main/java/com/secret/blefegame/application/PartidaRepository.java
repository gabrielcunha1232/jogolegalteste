package com.secret.blefegame.application;

import com.secret.blefegame.domain.Partida;

import java.util.Optional;

// Contrato de onde as partidas moram. A interface fica aqui (application);
// a implementação em memória fica em infra.memory. Trocar por Redis/banco no futuro = trocar a implementação.
public interface PartidaRepository {

    Optional<Partida> buscar(String codigoSala);

    void salvar(Partida partida);

    boolean existe(String codigoSala);

    void remover(String codigoSala);
}