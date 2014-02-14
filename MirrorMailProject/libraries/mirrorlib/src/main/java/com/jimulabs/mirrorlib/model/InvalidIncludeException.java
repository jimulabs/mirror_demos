package com.jimulabs.mirrorlib.model;

public class InvalidIncludeException extends Exception {
    public InvalidIncludeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidIncludeException(String message) {
        super(message);
    }
}
