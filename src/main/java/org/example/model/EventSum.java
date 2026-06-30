package org.example.model;

import java.time.LocalDate;

public class EventSum {
    private final Long id;
    private final String title;
    private final LocalDate eventDate;
    private final int maxSeats;
    private final int registeredCount;
    private final int availableSeats;

    public EventSum(Long id, String title, LocalDate eventDate, int maxSeats, int registeredCount) {
        this.id = id;
        this.title = title;
        this.eventDate = eventDate;
        this.maxSeats = maxSeats;
        this.registeredCount = registeredCount;
        this.availableSeats = Math.max(0, maxSeats - registeredCount);
    }

    public EventSum(Long id, String title, LocalDate eventDate, int maxSeats, int registeredCount, int availableSeats) {
        this.id = id;
        this.title = title;
        this.eventDate = eventDate;
        this.maxSeats = maxSeats;
        this.registeredCount = registeredCount;
        this.availableSeats = availableSeats;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }
}