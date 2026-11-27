package com.secret.blefegame.application;

import com.secret.blefegame.application.command.Comando;
import com.secret.blefegame.domain.Partida;
import com.secret.blefegame.domain.evento.EventoDominio;
import com.secret.blefegame.projector.PlayerStateProjector;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

// O "service" do seu MVC, só que com uma regra de ouro:
//   trava a partida -> executa -> tira a foto do estado -> DESTRAVA -> publica.
// Nunca faça I/O (publicar no STOMP) com o lock preso.
@Service
public class PartidaCommandHandler {

    private final PartidaRepository repository;
    private final EventPublisher publisher;
    private final PlayerStateProjector projector;

    // Decisão sua (pergunta 1 do guia): um lock por sala. O esqueleto assume ReentrantLock em um mapa.
    // Alternativas: synchronized no próprio objeto Partida, ou uma fila de comandos por partida.
    // Pense: o que cada uma garante quando dois jogadores clicam ao mesmo tempo?
    private final Map<String, ReentrantLock> locksPorSala = new ConcurrentHashMap<>();

    public PartidaCommandHandler(PartidaRepository repository, EventPublisher publisher, PlayerStateProjector projector) {
        // atribuir os três campos (validar que nenhum é nulo)
        throw new UnsupportedOperationException("implementar construtor");
    }

    public ResultadoComando tratar(Comando comando) {
        // validar que o comando não é nulo

        // buscar a partida no repositório; se não existir, lançar SalaNaoEncontradaException

        // obter o lock da sala (obterLock) e travar

        // dentro de try/finally (o unlock vai no finally, sempre):
        //   - executar o comando na partida: executar(partida, comando)  -> lista de eventos
        //   - tirar a foto do estado de TODOS os jogadores: projector.projetarParaTodos(partida)
        //     (isso precisa acontecer DENTRO do lock, porque a Partida muda)
        //   - se o comando foi tratado com sucesso, salvar a partida no repositório
        //   - montar o ResultadoComando com código da sala, eventos e estados

        // depois do finally (lock já solto): publisher.publicar(resultado)
        // e se a publicação lançar exceção? registre o erro e NÃO relance: a ação já aconteceu

        // retornar o resultado

        // sobre exceções do domínio: AcaoInvalidaException NÃO deve ser capturada aqui.
        // Deixe subir: a infra a transforma em mensagem para /user/queue/erros, só para quem errou.
        // Se a ação lança no meio, o lock ainda é solto (finally) e nada é publicado.
        throw new UnsupportedOperationException("implementar tratar");
    }

    private List<EventoDominio> executar(Partida partida, Comando comando) {
        // switch por padrão sobre o tipo do comando (sem default: o compilador garante que todos foram tratados)
        // para cada caso, chamar o método correspondente da Partida usando comando.jogadorId() como ator:
        //   Entrar               -> partida.entrar(new Inscricao(jogadorId, nome))
        //   Iniciar              -> partida.iniciar(jogadorId)
        //   NomearChanceler      -> partida.nomearChanceler(jogadorId, candidatoId)
        //   Votar                -> partida.votar(jogadorId, voto)
        //   PresidenteDescarta   -> partida.presidenteDescarta(jogadorId, indice)
        //   ChancelerPromulga    -> partida.chancelerPromulga(jogadorId, indice)
        //   ChancelerPropoeVeto  -> partida.chancelerPropoeVeto(jogadorId)
        //   PresidenteDecideVeto -> partida.presidenteDecideVeto(jogadorId, aceitar)
        //   InvestigarLealdade   -> partida.investigarLealdade(jogadorId, alvoId)
        //   ConvocarEleicaoEspecial -> partida.convocarEleicaoEspecial(jogadorId, novoPresidenteId)
        //   EspiarBaralho        -> partida.espiarBaralho(jogadorId)
        //   Executar             -> partida.executar(jogadorId, alvoId)
        throw new UnsupportedOperationException("implementar executar");
    }

    private ReentrantLock obterLock(String codigoSala) {
        // devolver o lock da sala, criando um novo se ainda não existir (computeIfAbsent)

        // pergunta: quando uma partida termina ou a sala é removida, esse lock também precisa ser removido,
        // senão o mapa cresce para sempre. Onde isso deve acontecer?
        throw new UnsupportedOperationException("implementar obterLock");
    }
}