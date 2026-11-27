package com.secret.blefegame.infra.auth;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// Liga um token secreto a (sala, jogador). O token é a "senha" do jogador: quem o tem, age como ele.
// O cliente guarda o token (para reconectar depois de um F5) e o envia no CONNECT do STOMP.
@Component
public class RegistroDeTokens {

    private final Map<String, Credencial> tokens = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String emitir(String codigoSala, UUID jogadorId) {
        Objects.requireNonNull(codigoSala, "codigo da sala nao pode ser nulo");
        Objects.requireNonNull(jogadorId, "jogador id nao pode ser nulo");

        byte[] bytes = new byte[32];
        this.random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        Credencial credencial = new Credencial(codigoSala, jogadorId);
        this.tokens.putIfAbsent(token, credencial);

        return token;
    }

    public Optional<Credencial> validar(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.tokens.get(token));
    }

    public void revogarDaSala(String codigoSala) {
        if (codigoSala == null) {
            return;
        }
        this.tokens.values().removeIf(credencial -> credencial.codigoSala().equals(codigoSala));
    }

}