package org.example.service;

import org.example.exception.DoubleRegistrationException;
import org.example.exception.EventNotFoundException;
import org.example.exception.NoAvailableSeatsException;
import org.example.exception.ValidationException;
import org.example.model.Event;
import org.example.model.EventDetails;
import org.example.model.EventSum;
import org.example.model.Participant;
import org.example.repository.EventRepository;
import org.example.repository.ParticipantRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.Locale;

public class EventService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    public EventService(EventRepository eventRepository, ParticipantRepository participantRepository) {
        this.eventRepository = Objects.requireNonNull(eventRepository, "eventRepository must not be null");
        this.participantRepository = Objects.requireNonNull(participantRepository, "participantRepository must not be null");
    }

    public List<EventSum> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents();
    }

    public EventDetails getEventDetails(long eventId) {
        validateEventId(eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        List<Participant> participants = participantRepository.findByEventId(eventId);

        return new EventDetails(event, participants);
    }

    public Event createEvent(String title, LocalDate eventDate, int maxSeats) {
        String normalizedTitle = validateTitle(title);
        validateEventDate(eventDate);
        validateMaxSeats(maxSeats);

        Event event = new Event(normalizedTitle, eventDate, maxSeats);

        return eventRepository.save(event);
    }

    public Participant registerParticipant(long eventId, String studentName, String studentEmail) {
        validateEventId(eventId);

        String normalizedName = validateStudentName(studentName);
        String normalizedEmail = validateStudentEmail(studentEmail);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        if (participantRepository.existsByEventIdAndEmail(eventId, normalizedEmail)) {
            throw new DoubleRegistrationException(normalizedEmail);
        }

        int registeredCount = participantRepository.countByEventId(eventId);

        if (registeredCount >= event.getMaxSeats()) {
            throw new NoAvailableSeatsException(eventId);
        }

        Participant participant = new Participant(eventId, normalizedName, normalizedEmail);

        return participantRepository.save(participant);
    }

    private void validateEventId(long eventId) {
        if (eventId <= 0) {
            throw new ValidationException("Event id must be greater than zero");
        }
    }

    private String validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Event title must not be blank");
        }

        return title.trim();
    }

    private void validateEventDate(LocalDate eventDate) {
        if (eventDate == null) {
            throw new ValidationException("Event date must not be null");
        }

        if (eventDate.isBefore(LocalDate.now())) {
            throw new ValidationException("Event date must not be in the past");
        }
    }

    private void validateMaxSeats(int maxSeats) {
        if (maxSeats <= 0) {
            throw new ValidationException("Max seats must be greater than zero");
        }
    }

    private String validateStudentName(String studentName) {
        if (studentName == null || studentName.isBlank()) {
            throw new ValidationException("Student name must not be blank");
        }

        return studentName.trim();
    }

    private String validateStudentEmail(String studentEmail) {
        if (studentEmail == null || studentEmail.isBlank()) {
            throw new ValidationException("Student email must not be blank");
        }

        String normalizedEmail = studentEmail.trim().toLowerCase(Locale.ROOT);

        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new ValidationException("Student email has invalid format");
        }

        return normalizedEmail;
    }
}