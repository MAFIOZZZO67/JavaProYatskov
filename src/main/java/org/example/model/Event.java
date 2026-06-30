package org.example.model;

import java.time.LocalDate;
import java.util.Objects;

public class Event {
    private Long id;
    private String title;
    private LocalDate eventDate;
    private int maxSeats;

    public Event() {
    }

    public Event(Long id, String title, LocalDate eventDate, int maxSeats) {
        this.id = id;
        this.title = title;
        this.eventDate = eventDate;
        this.maxSeats = maxSeats;
    }

    public Event(String title, LocalDate eventDate, int maxSeats) {
        this(null, title, eventDate, maxSeats);
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return maxSeats == event.maxSeats
                && Objects.equals(id, event.id)
                && Objects.equals(title, event.title)
                && Objects.equals(eventDate, event.eventDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, eventDate, maxSeats);
    }
}