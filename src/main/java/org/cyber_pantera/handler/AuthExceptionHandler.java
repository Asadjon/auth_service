package org.cyber_pantera.handler;

import io.jsonwebtoken.ExpiredJwtException;
import org.cyber_pantera.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> userNotFound(RuntimeException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFound(UserNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> userNotFound(EmailAlreadyExistsException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(EmailNotSentException.class)
    public ResponseEntity<?> emailNotSent(EmailNotSentException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> emailNotSent(InvalidCredentialsException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(EmailConfirmationException.class)
    public ResponseEntity<?> emailNotSent(EmailConfirmationException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> emailNotSent(ExpiredJwtException e) {
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(e.getMessage());
    }
}
