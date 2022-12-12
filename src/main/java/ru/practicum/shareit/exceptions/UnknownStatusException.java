package ru.practicum.shareit.exceptions;


public class UnknownStatusException extends RuntimeException {
    public UnknownStatusException(final String error) {
        super(error);
    }
}
