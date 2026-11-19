package com.secret.blefegame.domain;

import java.util.*;
import java.util.random.RandomGenerator;

public class Baralho {

    private final Deque<Faccao> compra;
    private final List<Faccao> descarte;
    private final RandomGenerator randomGenerator;

    Baralho(RandomGenerator random) {
        Objects.requireNonNull(random, "gerador nao pode ser vazio");


        List<Faccao> fila = new ArrayList<>(17);
        fila.addAll(Collections.nCopies(6, Faccao.LIBERAL));
        fila.addAll(Collections.nCopies(11, Faccao.FASCISTA));

        Collections.shuffle(fila, random);

        this.compra = new ArrayDeque<>(fila);
        this.descarte = new ArrayList<>();
        this.randomGenerator = random;

    }

    public List<Faccao> comprarTres() {
        if (this.compra.size() < 3) {
            reembaralharCartasDeDescarteNaCompra();
        }

        List<Faccao> topoCarta = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            topoCarta.add(this.compra.pollFirst());
        }

        return List.copyOf(topoCarta);
    }

    public Faccao comprarUma() {
        if (this.compra.isEmpty()) {
            reembaralharCartasDeDescarteNaCompra();
        }

        return this.compra.pollFirst();
    }

    public void descartar(Faccao faccao) {
        Objects.requireNonNull(faccao, "nao da para descar algo que esta vazio");

        this.descarte.add(faccao);
    }

    public List<Faccao> espiarTres() {
        if (this.compra.size() < 3) {
            reembaralharCartasDeDescarteNaCompra();
        }

        return this.compra.stream().limit(3).toList();
    }

    private void reembaralharCartasDeDescarteNaCompra() {
        if (totalCartasBaralho() == 0) {
            throw new IllegalStateException("nao existe mais uma quantidade razoavel de cartas para jogar");
        }

        Collections.shuffle(this.descarte, randomGenerator);
        this.compra.addAll(this.descarte);
        this.descarte.clear();
    }

    int totalCartasBaralho() {
        return this.compra.size() + this.descarte.size();
    }
}
