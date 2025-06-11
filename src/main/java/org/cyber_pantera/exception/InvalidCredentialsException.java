package org.cyber_pantera.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class InvalidCredentialsException extends RuntimeException {
    private final Set<String> credentials;

    public InvalidCredentialsException(Set<String> credentials) {
        super(String.join("\n", credentials));
        this.credentials = credentials;
    }
}
