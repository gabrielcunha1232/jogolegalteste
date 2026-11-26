package com.secret.blefegame.domain.evento;

public sealed interface EventoPublico extends EventoDominio permits JogadorEntrou, PartidaIniciada, ChancelerNomeado, VotoRegistrado,
        VotosRevelados, GovernoEleito, EleicaoRejeitada, FimDeJogo, PoliticaPromulgada, VetoProposto, VetoDecidido,
        PoderExecutivoLiberado, NovoPresidente {
}
