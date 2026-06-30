package org.example.exception;

public class NoAvailableSeatsException extends RuntimeException {
    public NoAvailableSeatsException(long eventId) {
        super("No seats available for event: " + eventId);
    }
}