package com.secret.blefegame.projector;

import com.secret.blefegame.domain.Partida;
import com.secret.blefegame.projector.dto.EstadoJogadorDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

// O lado "Query" do CQRS: lê a partida e produz o que cada jogador pode ver. Nunca altera nada.
//
// Decisão de design já embutida (pergunta 3 do guia): o projector NÃO lê a Partida direto.
// Ele pede à Partida uma VisaoPartida (partida.visaoPara(id)), que é montada DENTRO do domínio,
// onde o segredo mora. Assim o filtro do que cada um pode ver fica no domínio, o projector só traduz
// para DTO, e nenhum getter de papel precisa ficar público.
// Alternativa: colocar o projector no pacote domain e deixá-lo ler direto. Escolha uma e escreva um teste de vazamento.
@Component
public class PlayerStateProjector {

    public EstadoJogadorDTO projetar(Partida partida, UUID jogadorId) {
        // validar que partida e jogadorId não são nulos

        // pedir a visão desse jogador: partida.visaoPara(jogadorId)

        // converter cada JogadorVisao em JogadorDTO
        // e a VisaoPartida em EstadoJogadorDTO (enums viram String com name())
        // as cartas (Faccao) viram lista de String

        // retornar o DTO
        throw new UnsupportedOperationException("implementar projetar");
    }

    public Map<UUID, EstadoJogadorDTO> projetarParaTodos(Partida partida) {
        // validar que partida não é nula

        // para cada id em partida.idsParticipantes(), chamar projetar(partida, id)

        // retornar um mapa imutável id -> DTO
        throw new UnsupportedOperationException("implementar projetarParaTodos");
    }
}