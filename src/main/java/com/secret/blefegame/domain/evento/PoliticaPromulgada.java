package com.secret.blefegame.domain.evento;

import com.secret.blefegame.domain.Faccao;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;

public record PoliticaPromulgada(Faccao politica, boolean porCaos, int liberaisNoTabuleiro, int fascistasNoTabuleiro) implements EventoPublico{

    public PoliticaPromulgada {
        Objects.requireNonNull(politica, "carta nao pode ser nula");

        if (liberaisNoTabuleiro < 0 || fascistasNoTabuleiro < 0) {
            throw new AcaoInvalidaException("quantidade de cartas promulgadas nao podem estar negativas");
        }
    }
}
