package com.example.pcsbackend.exceptions;

public class UserNotExistsException extends RuntimeException {
    public UserNotExistsException(String email) {
        super("User (" + email + ") does not exist");
    }
}
