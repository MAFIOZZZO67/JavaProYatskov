package org.example.repository;

import org.example.model.Participant;

import java.util.List;

public interface ParticipantRepository {
    List<Participant> findByEventId(long eventId);

    int countByEventId(long eventId);

    Participant save(Participant participant);

    boolean existsByEventIdAndEmail(long eventId, String email);
}