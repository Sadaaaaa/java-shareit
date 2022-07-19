package ru.practicum.shareit.exception;

public class IncorrectItemRequestException extends RuntimeException {
    public IncorrectItemRequestException(String message) {
        super(message);
    }
}
