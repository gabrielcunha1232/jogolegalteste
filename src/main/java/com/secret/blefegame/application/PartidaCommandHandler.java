package com.secret.blefegame.application;

import com.secret.blefegame.application.command.Comando;
import com.secret.blefegame.application.excecao.SalaNaoEncontradaException;
import com.secret.blefegame.domain.Inscricao;
import com.secret.blefegame.domain.Partida;
import com.secret.blefegame.domain.evento.EventoDominio;
import com.secret.blefegame.projector.PlayerStateProjector;
import com.secret.blefegame.projector.dto.EstadoJogadorDTO;
import org.springframework.stereotype.Service;

import java.util.*;
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
        this.repository = Objects.requireNonNull(repository, "repositorio nao pode ser nulo");
        this.publisher = Objects.requireNonNull(publisher, "publisher nao pode ser nulo");
        this.projector = Objects.requireNonNull(projector, "projector nao pode ser nulo");
    }

    public ResultadoComando tratar(Comando comando) {
        Objects.requireNonNull(comando, "comando nao é nulo");

        Partida partida = this.repository.buscar(comando.codigoSala())
                .orElseThrow(() -> new SalaNaoEncontradaException("sala nao encontrada"));

        ReentrantLock lock = obterLock(comando.codigoSala());
        ResultadoComando resultado;
        lock.lock();

        try {
            List<EventoDominio> eventos = executar(partida, comando);
            Map<UUID, EstadoJogadorDTO> estados = this.projector.projetarParaTodos(partida);

            this.repository.salvar(partida);

            resultado = new ResultadoComando(comando.codigoSala(), eventos, estados);
        } finally {
            lock.unlock();
        }

        try {
            this.publisher.publicar(resultado);
        } catch (RuntimeException e) {

        }

        return resultado;
    }

    private List<EventoDominio> executar(Partida partida, Comando comando) {
        return switch (comando) {
            case Comando.Entrar c -> partida.entrar(new Inscricao(c.jogadorId(), c.nome()));
            case Comando.Iniciar c -> partida.iniciar(c.jogadorId());
            case Comando.NomearChanceler c -> partida.nomearChanceler(c.jogadorId(), c.candidatoId());
            case Comando.Votar c -> partida.votar(c.jogadorId(), c.voto());
            case Comando.PresidenteDescarta c -> partida.presidenteDescarta(c.jogadorId(), c.indice());
            case Comando.ChancelerPromulga c -> partida.chancelerPromulga(c.jogadorId(), c.indice());
            case Comando.ChancelerPropoeVeto c -> partida.chancelerPropoeVeto(c.jogadorId());
            case Comando.PresidenteDecideVeto c -> partida.presidenteDecideVeto(c.jogadorId(), c.aceitar());
            case Comando.InvestigarLealdade c -> partida.investigarLealdade(c.jogadorId(), c.alvoId());
            case Comando.ConvocarEleicaoEspecial c -> partida.convocarEleicaoEspecial(c.jogadorId(), c.novoPresidenteId());
            case Comando.EspiarBaralho c -> partida.espiarBaralho(c.jogadorId());
            case Comando.Executar c -> partida.executar(c.jogadorId(), c.alvoId());
        };
    }

    private ReentrantLock obterLock(String codigoSala) {
        return this.locksPorSala.computeIfAbsent(codigoSala, c -> new ReentrantLock());
    }
}