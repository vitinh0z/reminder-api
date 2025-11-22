package br.com.springnoobs.reminderapi.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ContactRequestDTO(@NotBlank String email, @NotBlank String phoneNumber) {}
