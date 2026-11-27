package com.secret.blefegame.infra.memory;

import com.secret.blefegame.application.PartidaRepository;
import com.secret.blefegame.domain.Partida;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Guarda as partidas na memória, indexadas pelo código da sala.
// O ConcurrentHashMap protege o MAPA (várias salas ao mesmo tempo); quem protege cada PARTIDA é o lock do handler.
@Repository
public class InMemoryPartidaRepository implements PartidaRepository {

    private final Map<String, Partida> partidas = new ConcurrentHashMap<>();

    @Override
    public Optional<Partida> buscar(String codigoSala) {
        // devolver a partida do código, se houver (Optional.ofNullable)
        throw new UnsupportedOperationException("implementar buscar");
    }

    @Override
    public void salvar(Partida partida) {
        // guardar no mapa usando partida.codigoSala() como chave
        // pense: salvar com um código que já existe sobrescreve. Isso é desejado aqui? E em criarSala?
        throw new UnsupportedOperationException("implementar salvar");
    }

    @Override
    public boolean existe(String codigoSala) {
        // dizer se o código existe no mapa
        throw new UnsupportedOperationException("implementar existe");
    }

    @Override
    public void remover(String codigoSala) {
        // remover a partida do mapa
        // pergunta: quem chama isso? partida terminada há X minutos? sala abandonada? Decida a política.
        throw new UnsupportedOperationException("implementar remover");
    }
}