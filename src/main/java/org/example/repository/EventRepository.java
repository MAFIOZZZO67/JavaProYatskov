package org.example.repository;

import org.example.model.Event;
import org.example.model.EventSum;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    List<EventSum> findUpcomingEvents();

    Optional<Event> findById(long id);

    Event save(Event event);
}