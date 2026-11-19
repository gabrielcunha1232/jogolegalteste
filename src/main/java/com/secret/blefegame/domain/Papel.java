package com.secret.blefegame.domain;

import java.util.Objects;

public record Papel(Faccao faccao, boolean ehHitler) {

    public Papel {
        Objects.requireNonNull(faccao, "A faccao nao pode ser nula");
        if (ehHitler && faccao != Faccao.FASCISTA) {
            throw new IllegalArgumentException("Invariante violada: Hitler não é fascista");
        }
    }

    public static Papel liberal() {
        return new Papel(Faccao.LIBERAL, false);
    }

    public static Papel fascista() {
        return new Papel(Faccao.FASCISTA, false);
    }

    public static Papel hitler() {
        return new Papel(Faccao.FASCISTA, true);
    }

    public boolean enxergarDuranteDistribuicao(int totalJogadores) {
        if (this.faccao == Faccao.LIBERAL) {
            return false;
        }

        if (this.ehHitler) {
            return totalJogadores <= 6;
        }

        return true;
    }

    public boolean ehFacista() {
        return this.faccao == Faccao.FASCISTA;
    }
}
