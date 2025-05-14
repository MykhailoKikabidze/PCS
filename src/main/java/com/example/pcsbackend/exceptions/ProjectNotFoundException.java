package com.example.pcsbackend.exceptions;

import java.util.UUID;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(UUID id) {
        super("Project with id (" + id.toString() + ") does not exists");
    }
}
