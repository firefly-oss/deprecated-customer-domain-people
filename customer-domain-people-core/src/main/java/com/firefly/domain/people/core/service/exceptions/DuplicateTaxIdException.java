package com.firefly.domain.people.core.service.exceptions;

public class DuplicateTaxIdException extends RuntimeException {
    public DuplicateTaxIdException(String message) {
        super(message);
    }
}
