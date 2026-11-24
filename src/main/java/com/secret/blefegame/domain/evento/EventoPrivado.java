package com.secret.blefegame.domain.evento;

import java.util.UUID;

public sealed interface EventoPrivado extends EventoDominio permits PapelDistribuido, CartasdoPresidente {

    UUID destinatarioId();
}
