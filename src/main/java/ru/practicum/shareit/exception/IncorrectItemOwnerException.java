package ru.practicum.shareit.exception;

public class IncorrectItemOwnerException extends RuntimeException {
    public IncorrectItemOwnerException(String message) {
        super(message);
    }
}
