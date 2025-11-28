package br.com.springnoobs.reminderapi.reminder.exception;

public class DueDateException extends RuntimeException {
    public DueDateException(String message) {
        super(message);
    }
}
