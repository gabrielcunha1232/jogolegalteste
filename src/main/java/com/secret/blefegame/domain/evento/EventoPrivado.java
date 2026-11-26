package com.secret.blefegame.domain.evento;

import java.util.UUID;

public sealed interface EventoPrivado extends EventoDominio permits PapelDistribuido, CartasDoPresidente, CartasDoChanceler,
        LealdadeInvestigada, CartasEspiadas {

    UUID destinatarioId();
}
