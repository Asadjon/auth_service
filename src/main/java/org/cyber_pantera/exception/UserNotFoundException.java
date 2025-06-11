package org.cyber_pantera.exception;

import org.cyber_pantera.entity.User;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super(email + " was not found");
    }
}
