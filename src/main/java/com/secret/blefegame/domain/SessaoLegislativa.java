package com.secret.blefegame.domain;

import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SessaoLegislativa {

    private final UUID idPresidente;
    private final UUID idChanceler;
    private final List<Faccao> cartasNaMao;
    private boolean vetoDisponivel;
    private Etapa etapa;

    SessaoLegislativa(UUID idPresidente, UUID idChanceler, boolean vetoDisponivel, List<Faccao> tresCartas) {
        Objects.requireNonNull(idPresidente, "id do presidente nao pode ser nulo");
        Objects.requireNonNull(idChanceler, "id do chanceler nao pode ser nulo");
        Objects.requireNonNull(tresCartas, "as tres cartas nao podem ser nulas");

        if (idPresidente.equals(idChanceler)) {
            throw new IllegalArgumentException("presidente e chanceler devem ser jogadores diferentes");
        }
        if (tresCartas.size() != 3) {
            throw new IllegalArgumentException("a sessao legislativa comeca com exatamente tres cartas");
        }

        this.idPresidente = idPresidente;
        this.idChanceler = idChanceler;
        this.cartasNaMao = new ArrayList<>(List.copyOf(tresCartas));
        this.vetoDisponivel = vetoDisponivel;
        this.etapa = Etapa.PRESIDENTE_DESCARTA;
    }

    Faccao presidenteDescarta(UUID jogadorId, int indice) {
        exigirEtapa(Etapa.PRESIDENTE_DESCARTA, "O presidente nao pode descartar nesta etapa.");
        exigirJogador(jogadorId, this.idPresidente, "Somente o presidente pode descartar nesta etapa.");
        validarIndice(indice);

        Faccao descartada = this.cartasNaMao.remove(indice);
        this.etapa = Etapa.CHANCELER_DECIDE;
        return descartada;
    }

    ResultadoPromulgacao chancelerPromulga(UUID jogadorId, int indice) {
        exigirEtapa(Etapa.CHANCELER_DECIDE, "O chanceler nao pode promulgar nesta etapa.");
        exigirJogador(jogadorId, this.idChanceler, "Somente o chanceler pode promulgar.");
        validarIndice(indice);

        Faccao promulgada = this.cartasNaMao.remove(indice);
        Faccao descartada = this.cartasNaMao.removeFirst();
        this.etapa = Etapa.CONCLUIDA;
        return new ResultadoPromulgacao(promulgada, descartada);
    }

    void chancelerPropoeVeto(UUID jogadorId) {
        exigirEtapa(Etapa.CHANCELER_DECIDE, "O chanceler nao pode propor veto nesta etapa.");
        exigirJogador(jogadorId, this.idChanceler, "Somente o chanceler pode propor veto.");
        if (!this.vetoDisponivel) {
            throw new AcaoInvalidaException("O veto nao esta disponivel (ou ja foi rejeitado nesta sessao).");
        }

        this.etapa = Etapa.PRESIDENTE_DECIDE_VETO;
    }

    boolean presidenteDecideVeto(UUID jogadorId, boolean aceitar) {
        exigirEtapa(Etapa.PRESIDENTE_DECIDE_VETO, "Nao ha veto aguardando decisao.");
        exigirJogador(jogadorId, this.idPresidente, "Somente o presidente pode decidir o veto.");

        if (aceitar) {
            this.etapa = Etapa.VETO_ACEITO;
            return true;
        }

        this.vetoDisponivel = false;
        this.etapa = Etapa.CHANCELER_DECIDE;
        return false;
    }

    List<Faccao> retirarCartasDescartadasPorVeto() {
        exigirEtapa(Etapa.VETO_ACEITO, "Nao ha cartas de veto aceito para retirar.");

        List<Faccao> descartadas = List.copyOf(this.cartasNaMao);
        this.cartasNaMao.clear();
        this.etapa = Etapa.CONCLUIDA;
        return descartadas;
    }

    public List<Faccao> cartasVisiveisPara(UUID jogadorId) {
        Objects.requireNonNull(jogadorId, "id do jogador nao pode ser nulo");

        boolean ehDono = donoDaMao().filter(jogadorId::equals).isPresent();
        return ehDono ? List.copyOf(this.cartasNaMao) : List.of();
    }

    public UUID idPresidente() {
        return this.idPresidente;
    }

    public UUID idChanceler() {
        return this.idChanceler;
    }

    public boolean vetoEstaDisponivel() {
        return this.vetoDisponivel;
    }

    public boolean aguardandoDecisaoDoVeto() {
        return this.etapa == Etapa.PRESIDENTE_DECIDE_VETO;
    }

    Etapa etapa() {
        return this.etapa;
    }

    private Optional<UUID> donoDaMao() {
        return switch (this.etapa) {
            case PRESIDENTE_DESCARTA -> Optional.of(this.idPresidente);
            case CHANCELER_DECIDE, PRESIDENTE_DECIDE_VETO -> Optional.of(this.idChanceler);
            case VETO_ACEITO, CONCLUIDA -> Optional.empty();
        };
    }

    private void exigirEtapa(Etapa esperada, String mensagem) {
        if (this.etapa != esperada) {
            throw new AcaoInvalidaException(mensagem);
        }
    }

    private static void exigirJogador(UUID jogadorId, UUID esperado, String mensagem) {
        Objects.requireNonNull(jogadorId, "id do jogador nao pode ser nulo");
        if (!esperado.equals(jogadorId)) {
            throw new AcaoInvalidaException(mensagem);
        }
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= this.cartasNaMao.size()) {
            throw new AcaoInvalidaException("O indice da carta esta fora dos limites da mao.");
        }
    }
}