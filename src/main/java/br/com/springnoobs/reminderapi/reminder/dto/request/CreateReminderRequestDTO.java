package br.com.springnoobs.reminderapi.reminder.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public record CreateReminderRequestDTO(
        @NotBlank(message = "Title must not be null") String title,
        @NotBlank(message = "RemindAt must not be null") Instant remindAt) {}
