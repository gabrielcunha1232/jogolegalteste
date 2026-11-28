package com.secret.blefegame.infra.rest;

import com.secret.blefegame.application.SalaService;
import com.secret.blefegame.infra.auth.RegistroDeTokens;
import com.secret.blefegame.infra.rest.dto.CredencialResponse;
import com.secret.blefegame.infra.rest.dto.CriarSalaRequest;
import com.secret.blefegame.infra.rest.dto.EntrarSalaRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/salas")
public class SalaRestController {

    private final SalaService salaService;
    private final RegistroDeTokens registroDeTokens;

    public SalaRestController(SalaService salaService, RegistroDeTokens registroDeTokens) {
        this.salaService = Objects.requireNonNull(salaService, "sala service nao pode ser nulo");
        this.registroDeTokens = Objects.requireNonNull(registroDeTokens, "registro de tokens nao pode ser nulo");
    }

    @PostMapping
    public ResponseEntity<CredencialResponse> criar(@RequestBody @Valid CriarSalaRequest request) {
        SalaService.SalaCriada salaCriada = this.salaService.criarSala(request.nome());
        String tokenAfitriao = this.registroDeTokens.emitir(salaCriada.codigoSala(), salaCriada.jogadorId());

        CredencialResponse credencialResponse = new CredencialResponse(salaCriada.codigoSala(), salaCriada.jogadorId(), tokenAfitriao);

        return ResponseEntity.status(HttpStatus.CREATED).body(credencialResponse);
    }

    @PostMapping("/{codigoSala}/jogadores")
    public ResponseEntity<CredencialResponse> entrar(@PathVariable String codigoSala,
                                                     @RequestBody @Valid EntrarSalaRequest request) {
        // entrar pelo salaService.entrarNaSala(codigoSala, request.nome()); devolve o id do jogador
        // (sala inexistente, lotada, nome repetido: as exceções sobem para o RestExceptionHandler)
        UUID jogadorId = this.salaService.entrarNaSala(codigoSala, request.nome());

        // emitir o token desse jogador
        String token = this.registroDeTokens.emitir(codigoSala, jogadorId);

        CredencialResponse credencialResponse = new CredencialResponse(codigoSala, jogadorId, token);

        // responder 201 com CredencialResponse
        return ResponseEntity.status(HttpStatus.CREATED).body(credencialResponse);
    }

    @GetMapping("/{codigoSala}")
    public ResponseEntity<Void> existe(@PathVariable String codigoSala) {
        if (salaService.salaExiste(codigoSala)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}