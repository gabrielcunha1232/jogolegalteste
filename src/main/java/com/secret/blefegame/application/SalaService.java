package com.secret.blefegame.application;

import com.secret.blefegame.application.command.Comando;
import com.secret.blefegame.domain.Partida;
import com.secret.blefegame.domain.excecao.AcaoInvalidaException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.UUID;

@Service
public class SalaService {

    public record SalaCriada(String codigoSala, UUID jogadorId) {
    }

    private final PartidaRepository repository;
    private final PartidaCommandHandler handler;
    private final SecureRandom random = new SecureRandom();

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TAMANHO_CODIGO = 5;
    private static final int MAX_TENTATIVAS = 20;

    public SalaService(PartidaRepository repository, PartidaCommandHandler handler) {
        this.repository = Objects.requireNonNull(repository, "repositorio nao pode estar nulo");
        this.handler = Objects.requireNonNull(handler, "handler nao pode estar nulo");
    }

    public SalaCriada criarSala(String nomeHost) {
        Objects.requireNonNull(nomeHost, "nome do host nao pode estar nulo");
        if (nomeHost.isBlank()) {
            throw new AcaoInvalidaException("Nome do host está vazio");
        }

        UUID novoIdAnfitriao = UUID.randomUUID();
        String codigoDaSala = gerarCodigoUnico();

        Partida novaPartida = new Partida(novoIdAnfitriao, nomeHost, codigoDaSala, this.random);
        this.repository.salvar(novaPartida);

        return new SalaCriada(codigoDaSala, novoIdAnfitriao);
    }

    public UUID entrarNaSala(String codigoSala, String nome) {
        Objects.requireNonNull(codigoSala, "codigo da sala nao pode estar vazio");
        Objects.requireNonNull(nome, "nome nao pode estar vazio");

        if (codigoSala.isBlank() || nome.isBlank()) {
            throw new AcaoInvalidaException("existem campos em branco");
        }

        UUID novoJogadorId = UUID.randomUUID();

        Comando.Entrar comando = new Comando.Entrar(codigoSala, novoJogadorId, nome);
        this.handler.tratar(comando);
        return novoJogadorId;
    }

    public boolean salaExiste(String codigoSala) {
        return this.repository.existe(codigoSala);
    }

    private String gerarCodigoUnico() {
        for (int tentativa = 0; tentativa < MAX_TENTATIVAS; tentativa++) {
            StringBuilder codigo = new StringBuilder(TAMANHO_CODIGO);

            for (int i = 0; i < TAMANHO_CODIGO; i++) {
                int indice = this.random.nextInt(ALFABETO.length());
                codigo.append(ALFABETO.charAt(indice));
            }

            String code = codigo.toString();

            if (!this.repository.existe(code)) {
                return code;
            }
        }

        throw new IllegalStateException("nao foi possivel gerar um codigo de sala unico");
    }
}