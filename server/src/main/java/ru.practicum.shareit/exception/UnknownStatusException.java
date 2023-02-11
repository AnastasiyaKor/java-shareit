package ru.practicum.shareit.exception;


public class UnknownStatusException extends RuntimeException {
    public UnknownStatusException(final String error) {
        super(error);
    }
}
