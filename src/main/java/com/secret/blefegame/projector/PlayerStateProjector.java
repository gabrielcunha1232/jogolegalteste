package com.secret.blefegame.projector;

import com.secret.blefegame.domain.Faccao;
import com.secret.blefegame.domain.Partida;
import com.secret.blefegame.domain.VisaoPartida;
import com.secret.blefegame.projector.dto.EstadoJogadorDTO;
import com.secret.blefegame.projector.dto.JogadorDTO;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PlayerStateProjector {

    public EstadoJogadorDTO projetar(Partida partida, UUID jogadorId) {
        Objects.requireNonNull(partida, "partida nao pode estar nula");
        Objects.requireNonNull(jogadorId, "jogador id nao pode estar nulo");

        VisaoPartida visao = partida.visaoPara(jogadorId);

        List<JogadorDTO> jogadoresDTO = visao.jogadores().stream()
                .map(j -> new JogadorDTO(j.id(), j.nome(), j.vivo(),
                        j.id().equals(visao.presidenteId()), j.id().equals(visao.chancelerId()),
                        j.jaVotou()))
                .toList();

        List<String> minhasCartasDTO = visao.minhasCartas().stream()
                .map(Enum::name)
                .toList();

        String minhaFaccaoDTO = visao.minhaFaccao() == null
                ? null
                : visao.minhaFaccao().name();


        return new EstadoJogadorDTO(
                jogadorId,
                visao.codigoSala(),
                visao.fase().name(),
                visao.anfitriaoId(),
                jogadoresDTO,
                visao.presidenteId(),
                visao.chancelerId(),
                visao.liberaisPromulgadas(),
                visao.fascistasPromulgadas(),
                visao.eleicoesFracassadas(),
                visao.poderPendente().name(),
                visao.vetoDisponivel(),
                minhaFaccaoDTO,
                visao.souHitler(),
                visao.aliadosVisiveis(),
                minhasCartasDTO
        );
    }

    public Map<UUID, EstadoJogadorDTO> projetarParaTodos(Partida partida) {
        Map<UUID, EstadoJogadorDTO> map = new HashMap<>();
        Objects.requireNonNull(partida, "partida nao pode estar nula");

        for (UUID id : partida.idsParticipantes()) {
            EstadoJogadorDTO dtoJogador = projetar(partida, id);
            map.putIfAbsent(id, dtoJogador);
        }

        return map;
    }
}