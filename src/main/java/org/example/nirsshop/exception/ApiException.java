package org.example.nirsshop.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final String message;
    private final int statusCode;

    public ApiException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}

