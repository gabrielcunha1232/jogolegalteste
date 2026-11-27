package com.secret.blefegame.domain;

import com.secret.blefegame.domain.evento.*;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.*;
import java.util.stream.Collectors;

public class Partida {

    private static final int MINIMO_JOGADORES = 5;
    private static final int MAXIMO_JOGADORES = 10;

    private final String codigoSala;
    private final Random randomGenerator;
    private final List<Inscricao> inscricoes;
    private final List<Jogador> jogadores;
    private FaseDoJogo fase;
    private UUID presidenteAtualId;
    private final UUID hostSala;

    private Baralho baralho;
    private Tabuleiro tabuleiro;
    private Governo governoAtual;
    private Governo governoAnteriorEleito;
    private Votacao votacao;
    private SessaoLegislativa sessaoLegislativa;
    private int eleicoesFracassadasSeguidas;

    private PoderExecutivo poderPendente;
    private UUID presidenteDaRotacaoId;
    private Set<UUID> investigados;

    public Partida(UUID hostSala, String nomeHost, String codigoSala, Random randomGenerator) {
        Objects.requireNonNull(hostSala, "id do host nao pode ser nulo");
        Objects.requireNonNull(nomeHost, "nome do host nao pode ser nulo");
        if (nomeHost.isBlank()) {
            throw new IllegalArgumentException("nome do host nao pode ser vazio");
        }
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

        this.poderPendente = PoderExecutivo.NENHUM;
        this.presidenteDaRotacaoId = null;
        this.investigados = new HashSet<>();

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
        this.presidenteDaRotacaoId = this.presidenteAtualId;
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
        for (Jogador j : this.jogadores) {
            Set<UUID> aliados = aliadosVisiveisDe(j);

            EventoDominio eventoDominio = new PapelDistribuido(j.id(), j.papel().faccao(),
                    j.papel().ehHitler(), new ArrayList<>(aliados));

            eventos.add(eventoDominio);
        }

        return eventos;
    }

    public List<EventoDominio> nomearChanceler(UUID presidenteId, UUID candidatoId) {
        List<EventoDominio> eventos = new ArrayList<>();

        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");
        Objects.requireNonNull(candidatoId, "id do candidato a chanceler nao pode ser nulo");

        if (this.fase != FaseDoJogo.ELEICAO_NOMEACAO) {
            throw new AcaoInvalidaException("fase do jogo deve ser de eleicao");
        }

        if (!this.presidenteAtualId.equals(presidenteId)) {
            throw new AcaoInvalidaException("o atual presidente nao é o mesmo do que quer eleger chanceler");
        }

        if (!candidatoElegivel(candidatoId)) {
            throw new AcaoInvalidaException("candidato nao pode ser eleito chanceler");
        }

        this.governoAtual = Governo.iniciarComPresidente(presidenteId).comChanceler(candidatoId);
        this.votacao = new Votacao(idsJogadoresVivos());
        this.fase = FaseDoJogo.ELEICAO_VOTACAO;

        eventos.add(new ChancelerNomeado(presidenteId, candidatoId));
        return eventos;
    }

    public List<EventoDominio> votar(UUID jogadorId, Voto voto) {
        List<EventoDominio> eventos = new ArrayList<>();

        Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
        Objects.requireNonNull(voto, "voto nao pode ser nulo");

        if (this.fase != FaseDoJogo.ELEICAO_VOTACAO) {
            throw new AcaoInvalidaException("fase nao coincide com o que precisamos");
        }

        this.votacao.registrar(jogadorId, voto);
        eventos.add(new VotoRegistrado(jogadorId, this.votacao.totalVotosComputados(), idsJogadoresVivos().size()));

        if (!this.votacao.completa()) {
            return eventos;
        }

        Map<UUID, Voto> revelacaoVotos = this.votacao.votosRevelados();
        boolean aprovado = this.votacao.aprovada();

        eventos.add(new VotosRevelados(revelacaoVotos, aprovado));
        this.votacao = null;

        if (aprovado) {
            eventos.addAll(aprovarGoverno());
        } else {
            eventos.addAll(rejeitarGoverno());
        }

        return eventos;
    }

    public List<EventoDominio> presidenteDescarta(UUID jogadorId, int indice) {
        List<EventoDominio> eventos = new ArrayList<>();
        Objects.requireNonNull(jogadorId, "jogadorId nao pode ser nulo");

        if (this.fase != FaseDoJogo.SESSAO_LEGISLATIVA_PRESIDENTE) {
            throw new AcaoInvalidaException("a fase do jogo deve ser correspondente com o descarte presidente");
        }

        UUID chancelerAtual = this.governoAtual.chancelerId();
        Objects.requireNonNull(chancelerAtual, "chanceler ainda nao existe");

        Faccao cartaDescartada = this.sessaoLegislativa.presidenteDescarta(jogadorId, indice);
        this.baralho.descartar(cartaDescartada);

        this.fase = FaseDoJogo.SESSAO_LEGISLATIVA_CHANCELER;
        List<Faccao> cartasVisiveis = this.sessaoLegislativa.cartasVisiveisPara(chancelerAtual);
        eventos.add(new CartasDoChanceler(chancelerAtual, cartasVisiveis));

        return eventos;
    }

    public List<EventoDominio> chancelerPromulga (UUID chancelerId, int indice) {
        List<EventoDominio> eventos = new ArrayList<>();
        Objects.requireNonNull(chancelerId, "chancelerId nao pode ser nulo");

        if (this.fase != FaseDoJogo.SESSAO_LEGISLATIVA_CHANCELER) {
            throw new AcaoInvalidaException("Fase do jogo não coincide com o que é necessário");
        }

        ResultadoPromulgacao promulgado = this.sessaoLegislativa.chancelerPromulga(chancelerId, indice);
        this.baralho.descartar(promulgado.descartada());

        PoderExecutivo poder = this.tabuleiro.promulgar(promulgado.promulgada());
        eventos.add(new PoliticaPromulgada(promulgado.promulgada(), false,
                this.tabuleiro.liberaisPromulgadas(), this.tabuleiro.fascistasPromulgadas()));

        this.sessaoLegislativa = null;

        Optional<FimDeJogo> fimDeJogo = verificarVitoriaPorPoliticas();
        if (fimDeJogo.isPresent()) {
            this.fase = FaseDoJogo.FIM_DE_JOGO;
            eventos.add(fimDeJogo.get());
            return eventos;
        }

        this.poderPendente = poder;
        if (poder != PoderExecutivo.NENHUM) {
            this.fase = FaseDoJogo.ACAO_EXECUTIVA;
            eventos.add(new PoderExecutivoLiberado(poder, this.presidenteAtualId));
            return eventos;
        }

        eventos.addAll(avancarProximaRodada());
        return eventos;
    }

    public List<EventoDominio> chancelerPropoeVeto(UUID jogadorId) {
        Objects.requireNonNull(jogadorId, "jogadorId nao pode ser nulo");

        if (this.fase != FaseDoJogo.SESSAO_LEGISLATIVA_CHANCELER) {
            throw new AcaoInvalidaException("fase do jogo nao coincide com sua etapa atual");
        }

        this.sessaoLegislativa.chancelerPropoeVeto(jogadorId);
        this.fase = FaseDoJogo.DECISAO_VETO_PRESIDENTE;

        return List.of(new VetoProposto(jogadorId));
    }

    public List<EventoDominio> presidenteDecideVeto(UUID jogadorId, boolean aceitar) {
        List<EventoDominio> evento = new ArrayList<>();
        Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");

        if (this.fase != FaseDoJogo.DECISAO_VETO_PRESIDENTE) {
            throw new AcaoInvalidaException("fase do jogo nao coincide com sua etapa atual");
        }

        boolean vetado = this.sessaoLegislativa.presidenteDecideVeto(jogadorId, aceitar);

        evento.add(new VetoDecidido(jogadorId, vetado));

        if (!vetado) {
            this.fase = FaseDoJogo.SESSAO_LEGISLATIVA_CHANCELER;
            return evento;
        }

        List<Faccao> cartasDeDescarte = this.sessaoLegislativa.retirarCartasDescartadasPorVeto();
        for (Faccao f : cartasDeDescarte) {
            this.baralho.descartar(f);
        }

        this.sessaoLegislativa = null;
        evento.addAll(rejeitarGoverno());

        return evento;
    }

    public List<Faccao> cartasNaMaoDe(UUID jogadorId) {
        if (this.sessaoLegislativa == null) {
            return List.of();
        }

        return this.sessaoLegislativa.cartasVisiveisPara(jogadorId);
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

    public List<EventoDominio> investigarLealdade(UUID presidenteId, UUID alvoId) {
        List<EventoDominio> eventos = new ArrayList<>();
        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");
        Objects.requireNonNull(alvoId, "alvo id nao pode ser nulo");

        if (presidenteId.equals(alvoId)) {
            throw new AcaoInvalidaException("alvo nao pode ser igual ao presidente");
        }

        exigirPoder(PoderExecutivo.INVESTIGAR_LEALDADE, presidenteId);
        Jogador alvo = buscarJogador(alvoId);

        if (!alvo.estaVivo()) {
            throw new AcaoInvalidaException("alvo tem que estar vivo");
        }

        if (this.investigados.contains(alvoId)) {
            throw new AcaoInvalidaException("alvo ja foi investigado antes");
        }
        this.investigados.add(alvoId);

        Faccao faccaoAlvo = alvo.papel().faccao();

        eventos.add(new JogadorInvestigado(presidenteId, alvoId));
        eventos.add(new LealdadeInvestigada(presidenteId, alvoId, faccaoAlvo));
        eventos.addAll(concluirAcaoExecutiva());
        return eventos;

    }

    public List<EventoDominio> convocarEleicaoEspecial(UUID presidenteId, UUID novoPresidenteId) {
        List<EventoDominio> eventos = new ArrayList<>();
        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");
        Objects.requireNonNull(novoPresidenteId, "novo presidente nao pode ser nulo");

        if (presidenteId.equals(novoPresidenteId)) {
            throw new AcaoInvalidaException("nenhum dos dois podem ser iguais");
        }

        exigirPoder(PoderExecutivo.ELEICAO_ESPECIAL, presidenteId);

        Jogador presidenteNovo = buscarJogador(novoPresidenteId);
        if (!presidenteNovo.estaVivo()) {
            throw new AcaoInvalidaException("o novo presidente deve estar vivo");
        }

        this.presidenteAtualId = novoPresidenteId;
        this.governoAtual = Governo.iniciarComPresidente(novoPresidenteId);
        this.poderPendente = PoderExecutivo.NENHUM;
        this.fase = FaseDoJogo.ELEICAO_NOMEACAO;

        eventos.add(new EleicaoEspecialConvocada(presidenteId, novoPresidenteId));

        return eventos;
    }

    public List<EventoDominio> espiarBaralho(UUID presidenteId) {
        List<EventoDominio> eventos = new ArrayList<>();
        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");

        exigirPoder(PoderExecutivo.ESPIAR_BARALHO, presidenteId);

        List<Faccao> tresCartasSuperiorBaralho = this.baralho.espiarTres();

        eventos.add(new BaralhoEspiado(presidenteId));
        eventos.add(new CartasEspiadas(presidenteId, tresCartasSuperiorBaralho));
        eventos.addAll(concluirAcaoExecutiva());

        return eventos;
    }

    public List<EventoDominio> executar(UUID presidenteId, UUID alvoId) {
        List<EventoDominio> eventos = new ArrayList<>();
        Objects.requireNonNull(presidenteId, "presidente id nao pode ser nulo");
        Objects.requireNonNull(alvoId, "alvo id nao pode ser nulo");

        if (presidenteId.equals(alvoId)) {
            throw new AcaoInvalidaException("os dois nao podem ser iguais");
        }

        exigirPoder(PoderExecutivo.EXECUCAO, presidenteId);

        Jogador alvo = buscarJogador(alvoId);
        alvo.executar();

        eventos.add(new JogadorExecutado(presidenteId, alvoId));

        if (alvo.papel().ehHitler()) {
            this.fase = FaseDoJogo.FIM_DE_JOGO;
            eventos.add(new FimDeJogo(Faccao.LIBERAL, MotivoVitoria.HITLER_EXECUTADO));
            return eventos;
        }

        eventos.addAll(concluirAcaoExecutiva());
        return eventos;
    }

    public PoderExecutivo poderPendente() {
        if (this.poderPendente == null) {
            this.poderPendente = PoderExecutivo.NENHUM;
        }

        return this.poderPendente;
    }

    public Set<UUID> idsParticipantes() {
        if (this.fase == FaseDoJogo.AGUARDANDO_JOGADORES) {
            return this.inscricoes.stream()
                    .map(Inscricao::id)
                    .collect(Collectors.toUnmodifiableSet());
        }

        return this.jogadores.stream().map(Jogador::id).collect(Collectors.toUnmodifiableSet());
    }

    public VisaoPartida visaoPara(UUID jogadorId) {
        Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");

        if (!idsParticipantes().contains(jogadorId)) {
            throw new AcaoInvalidaException("jogador nao esta no lobby");
        }

        List<JogadorVisao> jogadoresVisoes;

        if (this.fase == FaseDoJogo.AGUARDANDO_JOGADORES) {
            jogadoresVisoes = this.inscricoes.stream()
                    .map(i -> new JogadorVisao(i.id(), i.nome(), true, false))
                    .toList();
        } else {
            Set<UUID> idsQueVotaram = this.votacao == null
                    ? Set.of()
                    : this.votacao.jogadoresQueJaVotaram();

            jogadoresVisoes = this.jogadores.stream()
                    .map(j -> new JogadorVisao(j.id(), j.nome(),
                            j.estaVivo(), idsQueVotaram.contains(j.id())))
                    .toList();
        }

        int liberaisPromulgadas = this.tabuleiro == null
                ? 0
                : this.tabuleiro.liberaisPromulgadas();

        int fascistasPromulgadas = this.tabuleiro == null
                ? 0
                : this.tabuleiro.fascistasPromulgadas();

        boolean vetoDisponivel = this.tabuleiro != null
                && this.tabuleiro.vetoDisponivel();

        UUID chancelerId = this.governoAtual == null
                ? null
                : this.governoAtual.chancelerId();

        Faccao minhaFaccao = null;
        boolean ehHitler = false;
        Set<UUID> aliadosVisiveis = Set.of();
        List<Faccao> minhasCartas = List.of();

        if (this.fase != FaseDoJogo.AGUARDANDO_JOGADORES) {
            Jogador jogador = buscarJogador(jogadorId);

            minhaFaccao = jogador.papel().faccao();
            ehHitler = jogador.papel().ehHitler();
            aliadosVisiveis = aliadosVisiveisDe(jogador);
            minhasCartas = cartasNaMaoDe(jogadorId);
        }

        return new VisaoPartida(
                this.codigoSala,
                this.fase,
                this.hostSala,
                jogadoresVisoes,
                this.presidenteAtualId,
                chancelerId,
                liberaisPromulgadas,
                fascistasPromulgadas,
                this.eleicoesFracassadasSeguidas,
                this.poderPendente,
                vetoDisponivel,
                minhaFaccao,
                ehHitler,
                aliadosVisiveis,
                minhasCartas
        );
    }

    private void exigirPoder(PoderExecutivo poderExecutivo, UUID jogadorId) {
        if (this.fase != FaseDoJogo.ACAO_EXECUTIVA) {
            throw new AcaoInvalidaException("a fase deve condizir com a etapa para poder");
        }

        if (this.poderPendente != poderExecutivo) {
            throw new AcaoInvalidaException("poder executivo nao é o atual");
        }

        if (!this.presidenteAtualId.equals(jogadorId)) {
            throw new AcaoInvalidaException("o jogador referenciado nao é o atual presidente da rodada");
        }
    }

    private List<EventoDominio> concluirAcaoExecutiva() {
        this.poderPendente = PoderExecutivo.NENHUM;
        return avancarProximaRodada();
    }

    private List<EventoDominio> avancarProximaRodada() {
        List<EventoDominio> eventos = new ArrayList<>();

        this.presidenteAtualId = proximoPresidente();
        this.presidenteDaRotacaoId = this.presidenteAtualId;
        this.governoAtual = Governo.iniciarComPresidente(this.presidenteAtualId);
        this.sessaoLegislativa = null;
        this.fase = FaseDoJogo.ELEICAO_NOMEACAO;

        eventos.add(new NovoPresidente(this.presidenteAtualId));
        return eventos;
    }

    private boolean candidatoElegivel(UUID candidatoId) {
        if (this.presidenteAtualId.equals(candidatoId)) {
            return false;
        }

        if (!buscarJogador(candidatoId).estaVivo()) {
            return false;
        }

        if (this.governoAnteriorEleito != null) {

            if (idsJogadoresVivos().size() > 5 && this.governoAnteriorEleito.presidenteId().equals(candidatoId)) return false;

            return !this.governoAnteriorEleito.chancelerId().equals(candidatoId);
        }

        return true;
    }

    private List<EventoDominio> aprovarGoverno() {
        List<EventoDominio> eventos = new ArrayList<>();

        UUID chancelerId = this.governoAtual.chancelerId();
        if (this.tabuleiro.hitlerEleitoValeVitoria() && buscarJogador(chancelerId).papel().ehHitler()) {
            this.fase = FaseDoJogo.FIM_DE_JOGO;
            eventos.add(new FimDeJogo(Faccao.FASCISTA, MotivoVitoria.HITLER_ELEITO));
            return eventos;
        }

        this.eleicoesFracassadasSeguidas = 0;
        this.governoAnteriorEleito = this.governoAtual;

        eventos.add(new GovernoEleito(this.presidenteAtualId, this.governoAtual.chancelerId()));
        eventos.addAll(iniciarSessaoLegislativa());
        return eventos;
    }

    private List<EventoDominio> rejeitarGoverno() {
        List<EventoDominio> eventoDominios = new ArrayList<>();
        this.eleicoesFracassadasSeguidas++;
        int numEleicoesFracassadas = this.eleicoesFracassadasSeguidas;

        eventoDominios.add(new EleicaoRejeitada(proximoPresidente(), numEleicoesFracassadas));

        if (numEleicoesFracassadas == 3) {
            Faccao carta = this.baralho.comprarUma();
            this.tabuleiro.promulgar(carta);
            this.eleicoesFracassadasSeguidas = 0;
            this.governoAnteriorEleito = null;

            eventoDominios.add(new PoliticaPromulgada(carta, true,
                    this.tabuleiro.liberaisPromulgadas(), this.tabuleiro.fascistasPromulgadas()));

            Optional<FimDeJogo> fimDeJogo = verificarVitoriaPorPoliticas();
            if (fimDeJogo.isPresent()) {
                this.fase = FaseDoJogo.FIM_DE_JOGO;
                eventoDominios.add(fimDeJogo.get());
                return eventoDominios;
            }
        }

        eventoDominios.addAll(avancarProximaRodada());
        return eventoDominios;
    }

    private UUID proximoPresidente() {
        int total = this.jogadores.size();
        int jogadorIndice = this.jogadores.indexOf(buscarJogador(this.presidenteDaRotacaoId));
        for (int passo = 1; passo < total; passo++) {
            int proximoIndice = (jogadorIndice + passo) % total;
            Jogador candidato = this.jogadores.get(proximoIndice);

            if (candidato.estaVivo()) {
                return candidato.id();
            }
        }

        throw new AcaoInvalidaException("nenhum jogador está mais vivo");
    }

    private List<EventoDominio> iniciarSessaoLegislativa() {
        List<EventoDominio> eventos = new ArrayList<>();
        List<Faccao> tresCartasCompradas = this.baralho.comprarTres();

        this.sessaoLegislativa = new SessaoLegislativa(this.presidenteAtualId, this.governoAtual.chancelerId(),
                this.tabuleiro.vetoDisponivel(), tresCartasCompradas);
        this.fase = FaseDoJogo.SESSAO_LEGISLATIVA_PRESIDENTE;

        eventos.add(new CartasDoPresidente(this.presidenteAtualId, tresCartasCompradas));
        return eventos;
    }

    private Jogador buscarJogador(UUID jogadorId) {
        return this.jogadores.stream()
                .filter(j -> j.id().equals(jogadorId))
                .findFirst().orElseThrow(() -> new AcaoInvalidaException("jogador não está presente na lista"));
    }

    private Set<UUID> idsJogadoresVivos() {
        return Set.copyOf(
                this.jogadores.stream()
                .filter(Jogador::estaVivo)
                .map(Jogador::id)
                .toList()
        );
    }

    private Optional<FimDeJogo> verificarVitoriaPorPoliticas() {
        Optional<Faccao> vencedora = this.tabuleiro.vitoriaPorPoliticas();
        if (vencedora.isEmpty()) {
            return Optional.empty();
        }

        MotivoVitoria motivo = (vencedora.get() == Faccao.LIBERAL)
                ? MotivoVitoria.POLITICAS_LIBERAIS
                : MotivoVitoria.POLITICAS_FASCISTAS;

        return Optional.of(new FimDeJogo(vencedora.get(), motivo));
    }

    private Set<UUID> aliadosVisiveisDe(Jogador jogador) {
        int totalJogadores = this.jogadores.size();
        if (!jogador.papel().enxergarDuranteDistribuicao(totalJogadores)) {
            return Set.of();
        }

        return this.jogadores.stream()
                .filter(outro -> !outro.equals(jogador))
                .filter(outro -> outro.papel().ehFacista())
                .map(Jogador::id)
                .collect(Collectors.toUnmodifiableSet());
    }
}