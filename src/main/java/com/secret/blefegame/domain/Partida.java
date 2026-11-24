package com.secret.blefegame.domain;

import com.secret.blefegame.domain.evento.EventoDominio;

import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;

public class Partida {

    private static final int MINIMO_JOGADORES = 5;
    private static final int MAXIMO_JOGADORES = 10;

    private String codigoSala;
    private RandomGenerator randomGenerator;
    private List<Inscricao> inscricoes;
    private List<Jogador> jogadores;
    private FaseDoJogo fase;
    private UUID presidenteAtualId;

    public Partida(String codigoSala, RandomGenerator randomGenerator) {
        // validar codigoSala (não nulo, não em branco) e randomGenerator (não nulo)

        // atribuir os campos

        // inicializar inscricoes e jogadores como listas vazias e mutáveis

        // definir a fase inicial como AGUARDANDO_JOGADORES

        // presidenteAtualId fica nulo: só passa a existir depois de iniciar
    }

    public List<EventoDominio> entrar(Inscricao inscricao) {
        // validar que a inscricao não é nula

        // exigir fase == AGUARDANDO_JOGADORES, senão lançar AcaoInvalidaException

        // exigir que ainda haja vaga (menos de MAXIMO_JOGADORES inscritos)

        // exigir que o id da inscricao ainda não exista na lista
        // pense: nome repetido também deve ser barrado? como a mesa distingue dois "João"?

        // adicionar a inscricao à lista

        // retornar uma lista com o evento JogadorEntrou (id, nome, total de inscritos)
        throw new UnsupportedOperationException("implementar entrar");
    }

    public List<EventoDominio> iniciar() {
        // exigir fase == AGUARDANDO_JOGADORES

        // exigir entre MINIMO_JOGADORES e MAXIMO_JOGADORES inscritos (a validação do número mora aqui)
        // decida: só o criador da sala pode iniciar? se sim, este método recebe o id de quem pediu
        // e a Partida precisa guardar quem é o anfitrião

        // mudar a fase para DISTRIBUICAO_PAPEIS

        // sortear os papéis chamando sortearPapeis(total)

        // para cada inscricao, na ordem, criar um Jogador com o papel sorteado
        // (quando fizer isso, deixe o construtor de Jogador visível só dentro do pacote)

        // escolher o primeiro presidente com escolherPrimeiroPresidente()

        // montar os eventos: um PartidaIniciada (público) e, para cada jogador,
        // um PapelDistribuido (privado), usando montarEventosDePapeis()

        // decidir a fase seguinte: DISTRIBUICAO_PAPEIS é um estado de passagem?
        // se for, avance já para ELEICAO_NOMEACAO antes de retornar

        // retornar a lista com todos os eventos
        throw new UnsupportedOperationException("implementar iniciar");
    }

    private List<Papel> sortearPapeis(int totalJogadores) {
        // calcular quantos liberais, fascistas comuns e Hitler existem para este total:
        // 5 -> 3 liberais, 1 fascista | 6 -> 4, 1 | 7 -> 4, 2 | 8 -> 5, 2 | 9 -> 5, 3 | 10 -> 6, 3
        // e sempre exatamente 1 Hitler

        // montar uma lista com a quantidade certa de Papel (use as fábricas liberal(), fascista(), hitler())

        // embaralhar a lista usando o randomGenerator injetado (nunca um Random novo)

        // retornar a lista, com um papel para cada jogador
        throw new UnsupportedOperationException("implementar sortearPapeis");
    }

    private UUID escolherPrimeiroPresidente() {
        // sortear um jogador qualquer entre os jogadores criados, usando o randomGenerator injetado

        // retornar o id dele
        throw new UnsupportedOperationException("implementar escolherPrimeiroPresidente");
    }

    private List<EventoDominio> montarEventosDePapeis() {
        // para cada jogador:
        //   - perguntar ao papel dele se enxerga outros fascistas: papel.enxergarDuranteDistribuicao(total)
        //   - se enxerga: reunir os IDs dos outros jogadores cujo papel é fascista (o Hitler conta como fascista),
        //     excluindo o próprio jogador
        //   - se não enxerga: lista vazia
        //   - criar um PapelDistribuido com id, facção, ehHitler e a lista de ids

        // lembre: fascista comum vê todos os fascistas, incluindo o Hitler;
        // o Hitler só vê os fascistas com até 6 jogadores; liberal nunca vê ninguém

        // retornar a lista de eventos
        throw new UnsupportedOperationException("implementar montarEventosDePapeis");
    }

    public FaseDoJogo fase() {
        // retornar a fase atual
        throw new UnsupportedOperationException("implementar fase");
    }

    public String codigoSala() {
        // retornar o código da sala
        throw new UnsupportedOperationException("implementar codigoSala");
    }

    public int totalInscritos() {
        // retornar quantas inscrições existem
        throw new UnsupportedOperationException("implementar totalInscritos");
    }
}