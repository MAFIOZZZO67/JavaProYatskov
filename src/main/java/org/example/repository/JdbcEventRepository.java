package org.example.repository;

import org.example.config.DbConnection;
import org.example.model.Event;
import org.example.model.EventSum;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcEventRepository implements EventRepository {
    private static final String FIND_UPCOMING_EVENTS_SQL = """
            SELECT
                e.id,
                e.title,
                e.event_date,
                e.max_seats,
                COUNT(p.id) AS registered_count
            FROM events e
            LEFT JOIN participants p ON p.event_id = e.id
            WHERE e.event_date >= CURRENT_DATE
            GROUP BY e.id, e.title, e.event_date, e.max_seats
            ORDER BY e.event_date ASC
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id, title, event_date, max_seats
            FROM events
            WHERE id = ?
            """;

    private static final String SAVE_SQL = """
            INSERT INTO events (title, event_date, max_seats)
            VALUES (?, ?, ?)
            """;

    private final DbConnection connectionFactory;

    public JdbcEventRepository(DbConnection connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private void validateEventForSave(Event event) {
        if (event == null) {
            throw new IllegalStateException("Event must not be null");
        }

        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new IllegalStateException("Event title must not be blank");
        }

        if (event.getEventDate() == null) {
            throw new IllegalStateException("Event date must not be null");
        }

        if (event.getMaxSeats() <= 0) {
            throw new IllegalStateException("Event max seats must be greater than zero");
        }
    }

    @Override
    public List<EventSum> findUpcomingEvents() {
        List<EventSum> events = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_UPCOMING_EVENTS_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                events.add((EventSum) resultSet);
            }

            return events;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find upcoming events", e);
        }
    }

    @Override
    public Optional<Event> findById(long id) {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapEvent(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find event by id: " + id, e);
        }
    }

    @Override
    public Event save(Event event) {
        validateEventForSave(event);

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, event.getTitle());
            statement.setDate(2, Date.valueOf(event.getEventDate()));
            statement.setInt(3, event.getMaxSeats());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getLong(1));
                    return event;
                }

                throw new IllegalStateException("Failed to save event: generated id was not returned");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save event", e);
        }
    }

    private Event mapEvent(ResultSet resultSet) throws SQLException {
        return new Event(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getObject("event_date", LocalDate.class),
                resultSet.getInt("max_seats")
        );
    }

    private EventSum mapEventSummary(ResultSet resultSet) throws SQLException {
        return new EventSum(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getObject("event_date", LocalDate.class),
                resultSet.getInt("max_seats"),
                resultSet.getInt("registered_count")
        );
    }
}