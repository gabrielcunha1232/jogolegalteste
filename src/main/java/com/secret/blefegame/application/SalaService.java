package com.secret.blefegame.application;

import com.secret.blefegame.application.command.Comando;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

// Casos de uso fora do jogo em si: criar sala e entrar nela.
// Chamado pelos endpoints REST (POST /api/salas, POST /api/salas/{codigo}/jogadores).
@Service
public class SalaService {

    public record SalaCriada(String codigoSala, UUID jogadorId) {
    }

    private final PartidaRepository repository;
    private final PartidaCommandHandler handler;
    private final SecureRandom random = new SecureRandom();

    public SalaService(PartidaRepository repository, PartidaCommandHandler handler) {
        // atribuir repository e handler (validar que não são nulos)
        throw new UnsupportedOperationException("implementar construtor");
    }

    public SalaCriada criarSala(String nomeHost) {
        // validar nomeHost (não nulo, não em branco)

        // gerar um id novo para o anfitrião (UUID.randomUUID())

        // gerar um código de sala único: gerarCodigoUnico()

        // criar a Partida com (idDoAnfitriao, nomeHost, codigo, gerador de aleatoriedade)
        // sobre o gerador: em produção use SecureRandom (um Random previsível permitiria adivinhar papéis);
        // nos testes, use new Random(semente) para o sorteio ser reproduzível. Por isso ele é injetado.

        // salvar a partida no repositório

        // retornar SalaCriada com o código e o id do anfitrião
        // (o token que liga o cliente a esse id é responsabilidade da infra; não misture aqui)
        throw new UnsupportedOperationException("implementar criarSala");
    }

    public UUID entrarNaSala(String codigoSala, String nome) {
        // validar codigoSala e nome

        // gerar um id novo para o jogador

        // despachar um Comando.Entrar pelo handler (assim a regra de lock e publicação é a mesma de qualquer ação)

        // retornar o id gerado
        throw new UnsupportedOperationException("implementar entrarNaSala");
    }

    public boolean salaExiste(String codigoSala) {
        // delegar para o repositório
        throw new UnsupportedOperationException("implementar salaExiste");
    }

    private String gerarCodigoUnico() {
        // gerar um código curto (por exemplo 5 caracteres) com letras e números, evitando os ambíguos (0/O, 1/I)
        // usando o SecureRandom

        // se já existir no repositório, gerar outro (com um limite de tentativas para não ficar em laço infinito)

        // retornar o código
        throw new UnsupportedOperationException("implementar gerarCodigoUnico");
    }
}