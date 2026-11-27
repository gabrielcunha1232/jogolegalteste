package com.secret.blefegame.application;

import com.secret.blefegame.domain.Partida;

import java.util.Optional;

public interface PartidaRepository {

    Optional<Partida> buscar(String codigoSala);

    void salvar(Partida partida);

    boolean existe(String codigoSala);

    void remover(String codigoSala);
}