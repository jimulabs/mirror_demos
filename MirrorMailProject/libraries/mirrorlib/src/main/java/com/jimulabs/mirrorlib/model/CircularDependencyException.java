package com.jimulabs.mirrorlib.model;

public class CircularDependencyException extends Exception {

    public CircularDependencyException(String message) {
        super(message);
    }

    public CircularDependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
