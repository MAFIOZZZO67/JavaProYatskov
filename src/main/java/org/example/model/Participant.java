package org.example.model;

import java.util.Objects;

public class Participant {
    private Long id;
    private Long eventId;
    private String studentName;
    private String studentEmail;

    public Participant() {
    }

    public Participant(Long id, Long eventId, String studentName, String studentEmail) {
        this.id = id;
        this.eventId = eventId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
    }

    public Participant(Long eventId, String studentName, String studentEmail) {
        this(null, eventId, studentName, studentEmail);
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participant that)) return false;
        return Objects.equals(id, that.id)
                && Objects.equals(eventId, that.eventId)
                && Objects.equals(studentName, that.studentName)
                && Objects.equals(studentEmail, that.studentEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, studentName, studentEmail);
    }
}