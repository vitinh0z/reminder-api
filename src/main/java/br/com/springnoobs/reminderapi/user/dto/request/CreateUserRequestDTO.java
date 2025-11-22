package br.com.springnoobs.reminderapi.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequestDTO(
        @NotBlank String firstName, @NotBlank String lastName, ContactRequestDTO contactRequestDTO) {}
