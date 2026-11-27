package com.secret.blefegame.infra.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarSalaRequest(@NotBlank @Size(max = 20) String nome) {
}