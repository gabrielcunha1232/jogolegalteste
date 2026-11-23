package com.secret.blefegame.domain;

import java.util.Objects;
import java.util.Optional;

public class Tabuleiro {

    private final int totalJogadores;
    private int liberaisPromulgadas;
    private int fascistasPromulgadas;

    Tabuleiro(int totalJogadores) {
        if (totalJogadores < 5 || totalJogadores > 10) {
            throw new IllegalArgumentException("É preciso ter entre 5 e 10 jogadores.");
        }

        this.totalJogadores = totalJogadores;
        this.liberaisPromulgadas = 0;
        this.fascistasPromulgadas = 0;
    }

    public PoderExecutivo promulgar(Faccao faccao) {
        Objects.requireNonNull(faccao, "A política promulgada não pode ser nula.");

        if (vitoriaPorPoliticas().isPresent()) {
            throw new IllegalStateException("Não é possível promulgar políticas após o fim do jogo.");
        }

        if (faccao == Faccao.LIBERAL) {
            this.liberaisPromulgadas++;
            return PoderExecutivo.NENHUM;
        }

        this.fascistasPromulgadas++;
        return poderDaCasaFascista(this.fascistasPromulgadas);
    }

    public Optional<Faccao> vitoriaPorPoliticas() {
        if (this.liberaisPromulgadas >= 5) {
            return Optional.of(Faccao.LIBERAL);
        }

        if (this.fascistasPromulgadas >= 6) {
            return Optional.of(Faccao.FASCISTA);
        }

        return Optional.empty();
    }

    public boolean hitlerEleitoValeVitoria() {
        return this.fascistasPromulgadas >= 3;
    }

    public boolean vetoDisponivel() {
        return this.fascistasPromulgadas >= 5;
    }

    public int liberaisPromulgadas() {
        return this.liberaisPromulgadas;
    }

    public int fascistasPromulgadas() {
        return this.fascistasPromulgadas;
    }

    private PoderExecutivo poderDaCasaFascista(int casa) {
        if (totalJogadores <= 6) {
            return switch (casa) {
                case 3 -> PoderExecutivo.ESPIAR_BARALHO;
                case 4, 5 -> PoderExecutivo.EXECUCAO;
                default -> PoderExecutivo.NENHUM;
            };
        }

        if (totalJogadores <= 8) {
            return switch (casa) {
                case 2 -> PoderExecutivo.INVESTIGAR_LEALDADE;
                case 3 -> PoderExecutivo.ELEICAO_ESPECIAL;
                case 4, 5 -> PoderExecutivo.EXECUCAO;
                default -> PoderExecutivo.NENHUM;
            };
        }

        return switch (casa) {
            case 1, 2 -> PoderExecutivo.INVESTIGAR_LEALDADE;
            case 3 -> PoderExecutivo.ELEICAO_ESPECIAL;
            case 4, 5 -> PoderExecutivo.EXECUCAO;
            default -> PoderExecutivo.NENHUM;
        };
    }
}
