package com.secret.blefegame.domain;

import com.secret.blefegame.domain.evento.EventoDominio;
import com.secret.blefegame.domain.evento.JogadorEntrou;
import com.secret.blefegame.domain.evento.PapelDistribuido;
import com.secret.blefegame.domain.evento.PartidaIniciada;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.*;
import java.util.random.RandomGenerator;

public class Partida {

    private static final int MINIMO_JOGADORES = 5;
    private static final int MAXIMO_JOGADORES = 10;

    private final String codigoSala;
    private final RandomGenerator randomGenerator;
    private final List<Inscricao> inscricoes;
    private final List<Jogador> jogadores;
    private FaseDoJogo fase;
    private UUID presidenteAtualId;
    private UUID hostSala;

    public Partida(String codigoSala, RandomGenerator randomGenerator) {
        // validar codigoSala (não nulo, não em branco) e randomGenerator (não nulo)
        Objects.requireNonNull(codigoSala, "codigo nao pode ser nulo");
        if (codigoSala.isBlank()) {
            throw new AcaoInvalidaException("codigo da sala esta vazio");
        }
        Objects.requireNonNull(randomGenerator, "objeto de aletoriedade nao pode ser nulo");

        // atribuir os campos
        this.codigoSala = codigoSala;
        this.randomGenerator = randomGenerator;

        // inicializar inscricoes e jogadores como listas vazias e mutáveis
        this.inscricoes = new ArrayList<>();
        this.jogadores = new ArrayList<>();
        // definir a fase inicial como AGUARDANDO_JOGADORES
        this.fase = FaseDoJogo.AGUARDANDO_JOGADORES;
        // presidenteAtualId fica nulo: só passa a existir depois de iniciar
        this.presidenteAtualId = null;
        this.hostSala = null;
    }

    public List<EventoDominio> entrar(Inscricao inscricao) {
        // validar que a inscricao não é nula
        Objects.requireNonNull(inscricao, "inscricao nao pode estar nula");

        // exigir fase == AGUARDANDO_JOGADORES, senão lançar AcaoInvalidaException
        if (this.fase != FaseDoJogo.AGUARDANDO_JOGADORES) {
            throw new AcaoInvalidaException("a fase do jogo deve estar aguardando jogadores");
        }
        // exigir que ainda haja vaga (menos de MAXIMO_JOGADORES inscritos)
        if (this.inscricoes.size() >= MAXIMO_JOGADORES) {
            throw new AcaoInvalidaException("a sala ja atingiu o limite maximo de jogadores");
        }

        // exigir que o id da inscricao ainda não exista na lista
        // pense: nome repetido também deve ser barrado? como a mesa distingue dois "João"?
        for (Inscricao i : this.inscricoes) {
            if (i.id().equals(inscricao.id())) {
                throw new AcaoInvalidaException("ja existe esse inscrito no jogo");
            }
            if (i.nome().equalsIgnoreCase(inscricao.nome().trim())) {
                throw new AcaoInvalidaException("ja existe jogador com este nome");
            }
        }
        // adicionar a inscricao à lista
        this.inscricoes.add(inscricao);

        // retornar uma lista com o evento JogadorEntrou (id, nome, total de inscritos)
        return List.of(new JogadorEntrou(inscricao.id(), inscricao.nome(), this.inscricoes.size()));
    }

    public List<EventoDominio> iniciar(UUID solicitanteId) {
        // exigir fase == AGUARDANDO_JOGADORES
        if (this.fase != FaseDoJogo.AGUARDANDO_JOGADORES) {
            throw new AcaoInvalidaException("fase do jogo deve estar como aguardando jogadores");
        }

        // exigir entre MINIMO_JOGADORES e MAXIMO_JOGADORES inscritos (a validação do número mora aqui)
        if (totalInscritos() < MINIMO_JOGADORES || totalInscritos() > MAXIMO_JOGADORES) {
            throw new AcaoInvalidaException("está fora do padrão de inscritos. deve ser entre cinco e dez");
        }
        // decida: só o criador da sala pode iniciar? se sim, este metodo recebe o id de quem pediu
        // e a Partida precisa guardar quem é o anfitrião
        if (!this.hostSala.equals(solicitanteId)) {
            throw new AcaoInvalidaException("para iniciar deve ser o anfitriao");
        }
        // mudar a fase para DISTRIBUICAO_PAPEIS
        this.fase = FaseDoJogo.DISTRIBUICAO_PAPEIS;
        // sortear os papéis chamando sortearPapeis(total)
        List<Papel> papeis = sortearPapeis(totalInscritos());

        // para cada inscricao, na ordem, criar um Jogador com o papel sorteado
        // (quando fizer isso, deixe o construtor de Jogador visível só dentro do pacote)
        for (Inscricao i : this.inscricoes) {
            this.jogadores.add(new Jogador(i.id(), i.nome(), papeis.removeFirst()));
        }

        // escolher o primeiro presidente com escolherPrimeiroPresidente()
        this.presidenteAtualId = escolherPrimeiroPresidente();

        // montar os eventos: um PartidaIniciada (público) e, para cada jogador,
        // um PapelDistribuido (privado), usando montarEventosDePapeis()
        List<EventoDominio> eventos = new ArrayList<>();

        PartidaIniciada partidaIniciada = new PartidaIniciada(
                this.jogadores.size(),
                this.presidenteAtualId,
                this.jogadores.stream().map(Jogador::id).toList());

        eventos.add(partidaIniciada);
        eventos.addAll(montarEventosDePapeis());

        // decidir a fase seguinte: DISTRIBUICAO_PAPEIS é um estado de passagem?
        // se for, avance já para ELEICAO_NOMEACAO antes de retornar
        this.fase = FaseDoJogo.ELEICAO_NOMEACAO;

        // retornar a lista com todos os eventos
        return Collections.unmodifiableList(eventos);
    }

    private List<Papel> sortearPapeis(int totalJogadores) {
        int qntFacistas = 0;
        int qntLiberais = 0;
        // calcular quantos qntLiberais, fascistas comuns e Hitler existem para este total:
        // 5 -> 3 qntLiberais, 1 fascista | 6 -> 4, 1 | 7 -> 4, 2 | 8 -> 5, 2 | 9 -> 5, 3 | 10 -> 6, 3
        // e sempre exatamente 1 Hitler
        switch (totalJogadores) {
            case 5:
                qntFacistas = 1;
                qntLiberais = 3;
                break;
            case 6:
                qntFacistas = 1;
                qntLiberais = 4;
                break;
            case 7:
                qntFacistas = 2;
                qntLiberais = 4;
                break;
            case 8:
                qntFacistas = 2;
                qntLiberais = 5;
                break;
            case 9:
                qntFacistas = 2;
                qntLiberais = 6;
                break;
            case 10:
                qntFacistas = 3;
                qntLiberais = 6;
                break;
            default:
                throw new AcaoInvalidaException("Número de jogadores inválido: " + totalJogadores);
        }

        // montar uma lista com a quantidade certa de Papel (use as fábricas liberal(), fascista(), hitler())
        List<Papel> papeis = new ArrayList<>();
        papeis.addAll(Collections.nCopies(qntLiberais, Papel.liberal()));
        papeis.addAll(Collections.nCopies(qntFacistas, Papel.fascista()));
        papeis.add(Papel.hitler());

        // embaralhar a lista usando o randomGenerator injetado (nunca um Random novo)
        Collections.shuffle(papeis, this.randomGenerator);

        // retornar a lista, com um papel para cada jogador
        return papeis;
    }

    private UUID escolherPrimeiroPresidente() {
        // sortear um jogador qualquer entre os jogadores criados, usando o randomGenerator injetado
        int indiceSorteado = randomGenerator.nextInt(this.jogadores.size());
        Jogador jogadorSorteado = this.jogadores.get(indiceSorteado);

        // retornar o id dele
        return jogadorSorteado.id();
    }

    private List<EventoDominio> montarEventosDePapeis() {
        List<EventoDominio> eventos = new ArrayList<>();
        int totalJogadores = this.jogadores.size();

        // para cada jogador:
        //   - perguntar ao papel dele se enxerga outros fascistas: papel.enxergarDuranteDistribuicao(total)
        //   - se enxerga: reunir os IDs dos outros jogadores cujo papel é fascista (o Hitler conta como fascista),
        //     excluindo o próprio jogador
        //   - se não enxerga: lista vazia
        //   - criar um PapelDistribuido com id, facção, ehHitler e a lista de ids
        for (Jogador j : this.jogadores) {
            Papel papel = j.papel();
            List<UUID> enxegardos = List.of();

            if (papel.enxergarDuranteDistribuicao(totalJogadores)) {
                enxegardos = this.jogadores.stream()
                        .filter(outro -> !outro.equals(j))
                        .filter(outro -> outro.papel().ehFacista())
                        .map(Jogador::id)
                        .toList();
            }

            EventoDominio eventoDominio = new PapelDistribuido(j.id(), papel.faccao(), papel.ehHitler(), enxegardos);

            eventos.add(eventoDominio);
        }
        // lembre: fascista comum vê todos os fascistas, incluindo o Hitler;
        // o Hitler só vê os fascistas com até 6 jogadores; liberal nunca vê ninguém
        // retornar a lista de eventos
        return eventos;
    }

    public FaseDoJogo fase() {
        // retornar a fase atual
        return this.fase;
    }

    public String codigoSala() {
        // retornar o código da sala
        return this.codigoSala;
    }

    public int totalInscritos() {
        // retornar quantas inscrições existem
        return this.inscricoes.size();
    }
}