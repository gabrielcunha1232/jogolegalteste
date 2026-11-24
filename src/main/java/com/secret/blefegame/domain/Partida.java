package com.secret.blefegame.domain;

import com.secret.blefegame.domain.evento.*;
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

    private Baralho baralho;
    private Tabuleiro tabuleiro;
    private Governo governoAtual;
    private Governo governoAnteriorEleito;
    private Votacao votacao;
    private SessaoLegislativa sessaoLegislativa;
    private int eleicoesFracassadasSeguidas;

    public Partida(UUID hostSala, String nomeHost, String codigoSala, RandomGenerator randomGenerator) {
        Objects.requireNonNull(hostSala, "id do host nao pode ser nulo");
        Objects.requireNonNull(codigoSala, "codigo nao pode ser nulo");
        if (codigoSala.isBlank()) {
            throw new IllegalArgumentException("codigo da sala esta vazio");
        }
        Objects.requireNonNull(randomGenerator, "objeto de aletoriedade nao pode ser nulo");

        // atribuir os campos
        this.codigoSala = codigoSala;
        this.randomGenerator = randomGenerator;

        this.inscricoes = new ArrayList<>();
        this.jogadores = new ArrayList<>();
        this.fase = FaseDoJogo.AGUARDANDO_JOGADORES;
        this.presidenteAtualId = null;

        this.baralho = null;
        this.tabuleiro = null;
        this.governoAtual = null;
        this.governoAnteriorEleito = null;
        this.votacao = null;
        this.sessaoLegislativa = null;
        this.eleicoesFracassadasSeguidas = 0;

        inscricoes.add(new Inscricao(hostSala, nomeHost));
        this.hostSala = hostSala;
    }

    public List<EventoDominio> entrar(Inscricao inscricao) {
        Objects.requireNonNull(inscricao, "inscricao nao pode estar nula");

        if (this.fase != FaseDoJogo.AGUARDANDO_JOGADORES) {
            throw new AcaoInvalidaException("a fase do jogo deve estar aguardando jogadores");
        }
        if (this.inscricoes.size() >= MAXIMO_JOGADORES) {
            throw new AcaoInvalidaException("a sala ja atingiu o limite maximo de jogadores");
        }

        for (Inscricao i : this.inscricoes) {
            if (i.id().equals(inscricao.id())) {
                throw new AcaoInvalidaException("ja existe esse inscrito no jogo");
            }
            if (i.nome().equalsIgnoreCase(inscricao.nome().trim())) {
                throw new AcaoInvalidaException("ja existe jogador com este nome");
            }
        }
        this.inscricoes.add(inscricao);

        return List.of(new JogadorEntrou(inscricao.id(), inscricao.nome(), this.inscricoes.size()));
    }

    public List<EventoDominio> iniciar(UUID solicitanteId) {
        if (this.fase != FaseDoJogo.AGUARDANDO_JOGADORES) {
            throw new AcaoInvalidaException("fase do jogo deve estar como aguardando jogadores");
        }

        if (totalInscritos() < MINIMO_JOGADORES || totalInscritos() > MAXIMO_JOGADORES) {
            throw new AcaoInvalidaException("está fora do padrão de inscritos. deve ser entre cinco e dez");
        }
        if (!this.hostSala.equals(solicitanteId)) {
            throw new AcaoInvalidaException("para iniciar deve ser o anfitriao");
        }
        List<Papel> papeis = sortearPapeis(totalInscritos());
        for (Inscricao i : this.inscricoes) {
            this.jogadores.add(new Jogador(i.id(), i.nome(), papeis.removeFirst()));
        }
        this.presidenteAtualId = escolherPrimeiroPresidente();
        this.fase = FaseDoJogo.DISTRIBUICAO_PAPEIS;

        this.baralho = new Baralho(this.randomGenerator);
        this.tabuleiro = new Tabuleiro(this.jogadores.size());
        this.governoAtual = Governo.iniciarComPresidente(this.presidenteAtualId);

        List<EventoDominio> eventos = new ArrayList<>();

        PartidaIniciada partidaIniciada = new PartidaIniciada(
                this.jogadores.size(),
                this.presidenteAtualId,
                this.jogadores.stream().map(Jogador::id).toList());

        eventos.add(partidaIniciada);
        eventos.addAll(montarEventosDePapeis());

        this.fase = FaseDoJogo.ELEICAO_NOMEACAO;

        return Collections.unmodifiableList(eventos);
    }

    private List<Papel> sortearPapeis(int totalJogadores) {
        int qntFacistas = 0;
        int qntLiberais = 0;
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
                throw new IllegalArgumentException("Número de jogadores inválido: " + totalJogadores);
        }

        List<Papel> papeis = new ArrayList<>();
        papeis.addAll(Collections.nCopies(qntLiberais, Papel.liberal()));
        papeis.addAll(Collections.nCopies(qntFacistas, Papel.fascista()));
        papeis.add(Papel.hitler());

        Collections.shuffle(papeis, this.randomGenerator);

        if (papeis.size() != totalJogadores) {
            throw new IllegalStateException("o que foi sorteado nao esta de acordo com o total de jogadores");
        }

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
        for (Jogador j : this.jogadores) {
            Papel papel = j.papel();
            List<UUID> enxergados = List.of();

            if (papel.enxergarDuranteDistribuicao(totalJogadores)) {
                enxergados = this.jogadores.stream()
                        .filter(outro -> !outro.equals(j))
                        .filter(outro -> outro.papel().ehFacista())
                        .map(Jogador::id)
                        .toList();
            }

            EventoDominio eventoDominio = new PapelDistribuido(j.id(), papel.faccao(), papel.ehHitler(), enxergados);

            eventos.add(eventoDominio);
        }
        return eventos;
    }

    public List<EventoDominio> nomearChanceler(UUID presidenteId, UUID candidatoId) {
        List<EventoDominio> eventos = new ArrayList<>();

        // validar que presidenteId e candidatoId não são nulos
        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");
        Objects.requireNonNull(candidatoId, "id do candidato a chanceler nao pode ser nulo");

        // exigir fase == ELEICAO_NOMEACAO, senão AcaoInvalidaException
        if (this.fase != FaseDoJogo.ELEICAO_NOMEACAO) {
            throw new AcaoInvalidaException("fase do jogo deve ser de eleicao");
        }
        // exigir que presidenteId seja o presidenteAtualId (só o presidente nomeia)
        if (!this.presidenteAtualId.equals(presidenteId)) {
            throw new AcaoInvalidaException("o atual presidente nao é o mesmo do que quer eleger chanceler");
        }
        // buscar o candidato com buscarJogador(candidatoId); se não existir, AcaoInvalidaException
        Jogador jogadorNoTabuleiro = buscarJogador(candidatoId);
        Objects.requireNonNull(jogadorNoTabuleiro, "jogador nao existe na lista");
        // exigir que o candidato seja elegível: candidatoElegivel(candidatoId)
        if (!candidatoElegivel(candidatoId)) {
            throw new IllegalArgumentException("candidato nao pode ser eleito chanceler");
        }
        // montar o governo: Governo.iniciarComPresidente(presidenteId).comChanceler(candidatoId)
        Governo.iniciarComPresidente(presidenteId).comChanceler(candidatoId);
        // abrir a votação: new Votacao(idsJogadoresVivos())
        Votacao novaVotacao = new Votacao(idsJogadoresVivos());

        // mudar a fase para ELEICAO_VOTACAO
        this.fase = FaseDoJogo.ELEICAO_VOTACAO;
        // retornar uma lista com o evento ChancelerNomeado
        eventos.add(new GovernoEleito(presidenteId, candidatoId));
        return eventos;
    }

    public List<EventoDominio> votar(UUID jogadorId, Voto voto) {
        // validar que jogadorId e voto não são nulos

        // exigir fase == ELEICAO_VOTACAO

        // registrar o voto na votacao (a Votacao já barra voto duplicado e quem não pode votar)

        // criar o evento VotoRegistrado (quem votou e quantos já votaram, NUNCA o voto)
        // dica: a Votacao hoje só expõe totalVotosComputados(); você precisa também do total esperado.
        // decida se adiciona esse método à Votacao

        // se a votacao ainda não está completa, retornar só o VotoRegistrado

        // se está completa:
        //   - pegar os votos revelados e o resultado (aprovada ou não)
        //   - criar o evento VotosRevelados
        //   - zerar a votacao (this.votacao = null)
        //   - se aprovada, juntar os eventos de aprovarGoverno()
        //   - se rejeitada, juntar os eventos de rejeitarGoverno()

        // retornar a lista com todos os eventos, na ordem em que aconteceram
        throw new UnsupportedOperationException("implementar votar");
    }

    public FaseDoJogo fase() {
        return this.fase;
    }

    public String codigoSala() {
        return this.codigoSala;
    }

    public int totalInscritos() {
        return this.inscricoes.size();
    }

    private boolean candidatoElegivel(UUID candidatoId) {
        if (this.presidenteAtualId.equals(candidatoId)) {
            return false;
        }

        if (this.governoAnteriorEleito.chancelerId().equals(candidatoId)) {
            return false;
        }

        return !this.governoAnteriorEleito.presidenteId().equals(candidatoId) || idsJogadoresVivos().size() <= 5;
    }

    private Jogador buscarJogador(UUID jogadorId) {
        return this.jogadores.stream()
                .filter(j -> j.id().equals(jogadorId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("jogador não está presente na lista"));
    }

    private Set<UUID> idsJogadoresVivos() {
        return Set.copyOf(
                this.jogadores.stream()
                .map(Jogador::id)
                .toList()
        );
    }
}