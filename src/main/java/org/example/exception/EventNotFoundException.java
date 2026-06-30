package org.example.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(long eventId) {
        super("Event not found: " + eventId);
    }
}