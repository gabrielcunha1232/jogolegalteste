package com.secret.blefegame.application;

// Contrato de como o resultado chega aos clientes. O domínio e o handler não sabem que existe STOMP:
// quem implementa (infra.websocket.StompEventPublisher) é que usa o SimpMessagingTemplate.
//
// Regras do contrato:
//  - é chamado SEMPRE depois de o lock da partida ter sido solto;
//  - uma falha ao publicar não desfaz a ação (o estado do jogo já mudou): registre o erro e siga;
//  - a implementação decide o destino pelo TIPO do evento (EventoPublico -> tópico da sala,
//    EventoPrivado -> fila do destinatário) e envia o EstadoJogadorDTO de cada jogador.
public interface EventPublisher {

    void publicar(ResultadoComando resultado);
}