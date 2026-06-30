package org.example.repository;

import org.example.config.DbConnection;
import org.example.model.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcParticipantRepository implements ParticipantRepository {
    private static final String FIND_BY_EVENT_ID_SQL = """
            SELECT id, event_id, student_name, student_email
            FROM participants
            WHERE event_id = ?
            ORDER BY id ASC
            """;

    private static final String COUNT_BY_EVENT_ID_SQL = """
            SELECT COUNT(*) AS participants_count
            FROM participants
            WHERE event_id = ?
            """;

    private static final String SAVE_SQL = """
            INSERT INTO participants (event_id, student_name, student_email)
            VALUES (?, ?, ?)
            """;

    private static final String EXISTS_BY_EVENT_ID_AND_EMAIL_SQL = """
            SELECT 1
            FROM participants
            WHERE event_id = ? AND LOWER(student_email) = LOWER(?)
            """;

    private final DbConnection connectionFactory;

    public JdbcParticipantRepository(DbConnection connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private void validateParticipantForSave(Participant participant) {
        if (participant == null) {
            throw new IllegalStateException("Participant must not be null");
        }

        if (participant.getEventId() == null) {
            throw new IllegalStateException("Participant event id must not be null");
        }

        if (participant.getStudentName() == null || participant.getStudentName().isBlank()) {
            throw new IllegalStateException("Participant student name must not be blank");
        }

        if (participant.getStudentEmail() == null || participant.getStudentEmail().isBlank()) {
            throw new IllegalStateException("Participant student email must not be blank");
        }
    }

    @Override
    public List<Participant> findByEventId(long eventId) {
        List<Participant> participants = new ArrayList<>();

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EVENT_ID_SQL)) {

            statement.setLong(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    participants.add(mapParticipant(resultSet));
                }
            }

            return participants;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find participants by event id: " + eventId, e);
        }
    }

    @Override
    public int countByEventId(long eventId) {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(COUNT_BY_EVENT_ID_SQL)) {

            statement.setLong(1, eventId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("participants_count");
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to count participants by event id: " + eventId, e);
        }
    }

    @Override
    public Participant save(Participant participant) {
        validateParticipantForSave(participant);

        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, participant.getEventId());
            statement.setString(2, participant.getStudentName());
            statement.setString(3, participant.getStudentEmail());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    participant.setId(generatedKeys.getLong(1));
                    return participant;
                }

                throw new IllegalStateException("Failed to save participant: generated id was not returned");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to save participant", e);
        }
    }

    @Override
    public boolean existsByEventIdAndEmail(long eventId, String email) {
        try (Connection connection = connectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS_BY_EVENT_ID_AND_EMAIL_SQL)) {

            statement.setLong(1, eventId);
            statement.setString(2, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check participant email for event id: " + eventId, e);
        }
    }

    private Participant mapParticipant(ResultSet resultSet) throws SQLException {
        return new Participant(
                resultSet.getLong("id"),
                resultSet.getLong("event_id"),
                resultSet.getString("student_name"),
                resultSet.getString("student_email")
        );
    }
}