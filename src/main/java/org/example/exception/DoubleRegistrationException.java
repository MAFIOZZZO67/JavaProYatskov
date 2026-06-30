package org.example.exception;

public class DoubleRegistrationException extends RuntimeException {
    public DoubleRegistrationException(String email) {
        super("Student is already registered for this event");
    }
}