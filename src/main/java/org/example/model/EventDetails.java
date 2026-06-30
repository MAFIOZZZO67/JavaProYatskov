package org.example.model;

import org.example.model.Event;
import org.example.model.Participant;

import java.util.List;
import java.util.Objects;

public class EventDetails {
    private final Event event;
    private final List<Participant> participants;
    private final int availableSeats;

    public EventDetails(Event event, List<Participant> participants) {
        this.event = Objects.requireNonNull(event, "event must not be null");
        this.participants = List.copyOf(
                Objects.requireNonNull(participants, "participants must not be null")
        );
        this.availableSeats = Math.max(0, event.getMaxSeats() - participants.size());
    }

    public Event getEvent() {
        return event;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }
}