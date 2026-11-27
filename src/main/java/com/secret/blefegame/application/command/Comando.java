package com.secret.blefegame.application.command;

import com.secret.blefegame.domain.Voto;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;

import java.util.Objects;
import java.util.UUID;

public sealed interface Comando {

    String codigoSala();

    UUID jogadorId();

    record Entrar(String codigoSala, UUID jogadorId, String nome) implements Comando {
        public Entrar {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
            Objects.requireNonNull(nome, "nome nao pode ser nulo");

            if (nome.isBlank()) {
                throw new AcaoInvalidaException("nome está em branco");
            }
        }
    }

    record Iniciar(String codigoSala, UUID jogadorId) implements Comando {
        public Iniciar {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
        }
    }

    record NomearChanceler(String codigoSala, UUID jogadorId, UUID candidatoId) implements Comando {
        public NomearChanceler {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
            Objects.requireNonNull(candidatoId, "candidato id nao pode ser nulo");
        }
    }

    record Votar(String codigoSala, UUID jogadorId, Voto voto) implements Comando {
        public Votar {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
            Objects.requireNonNull(voto, "voto nao pode ser nulo");
        }
    }

    record PresidenteDescarta(String codigoSala, UUID jogadorId, int indice) implements Comando {
        public PresidenteDescarta {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
        }
    }

    record ChancelerPromulga(String codigoSala, UUID jogadorId, int indice) implements Comando {
        public ChancelerPromulga {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
        }
    }

    record ChancelerPropoeVeto(String codigoSala, UUID jogadorId) implements Comando {
        public ChancelerPropoeVeto {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
        }
    }

    record PresidenteDecideVeto(String codigoSala, UUID jogadorId, boolean aceitar) implements Comando {
        public PresidenteDecideVeto {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
        }
    }

    record InvestigarLealdade(String codigoSala, UUID jogadorId, UUID alvoId) implements Comando {
        public InvestigarLealdade {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
            Objects.requireNonNull(alvoId, "alvo id nao pode ser nulo");
        }
    }

    record ConvocarEleicaoEspecial(String codigoSala, UUID jogadorId, UUID novoPresidenteId) implements Comando {
        public ConvocarEleicaoEspecial {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
            Objects.requireNonNull(novoPresidenteId, "novo presidente id nao pode ser nulo");
        }
    }

    record EspiarBaralho(String codigoSala, UUID jogadorId) implements Comando {
        public EspiarBaralho {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
        }
    }

    record Executar(String codigoSala, UUID jogadorId, UUID alvoId) implements Comando {
        public Executar {
            Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
            Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");
            Objects.requireNonNull(alvoId, "alvo id nao pode ser nulo");
        }
    }
}