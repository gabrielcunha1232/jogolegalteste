package com.secret.blefegame.infra.memory;

import com.secret.blefegame.application.PartidaRepository;
import com.secret.blefegame.domain.Partida;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPartidaRepository implements PartidaRepository {

    private final Map<String, Partida> partidas = new ConcurrentHashMap<>();

    @Override
    public Optional<Partida> buscar(String codigoSala) {
        return Optional.ofNullable(this.partidas.get(codigoSala));
    }

    @Override
    public void salvar(Partida partida) {
        if (this.partidas.putIfAbsent(partida.codigoSala(), partida) != null) {
            throw new AcaoInvalidaException("codigo da sala ja existe");
        }
    }

    @Override
    public boolean existe(String codigoSala) {
        return this.partidas.containsKey(codigoSala);
    }

    @Override
    public void remover(String codigoSala) {
        this.partidas.remove(codigoSala);
    }
}