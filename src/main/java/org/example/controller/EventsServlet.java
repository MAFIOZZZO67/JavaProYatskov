package org.example.controller;

import org.example.config.DbConnection;
import org.example.exception.ValidationException;
import org.example.repository.EventRepository;
import org.example.repository.JdbcEventRepository;
import org.example.repository.JdbcParticipantRepository;
import org.example.repository.ParticipantRepository;
import org.example.service.EventService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/events")
public class EventsServlet extends HttpServlet {
    private static final String EVENTS_VIEW = "/WEB-INF/views/events.jsp";

    private EventService eventService;

    @Override
    public void init() {
        DbConnection connectionFactory = new DbConnection();
        EventRepository eventRepository = new JdbcEventRepository(connectionFactory);
        ParticipantRepository participantRepository = new JdbcParticipantRepository(connectionFactory);

        this.eventService = new EventService(eventRepository, participantRepository);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        showEventsPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String title = request.getParameter("title");
        String eventDateValue = request.getParameter("eventDate");
        String maxSeatsValue = request.getParameter("maxSeats");

        try {
            LocalDate eventDate = parseEventDate(eventDateValue);
            int maxSeats = Integer.parseInt(maxSeatsValue);

            eventService.createEvent(title, eventDate, maxSeats);

            response.sendRedirect(request.getContextPath() + "/events");
        } catch (ValidationException | DateTimeParseException | NumberFormatException e) {
            request.setAttribute("errorMessage", "Please provide valid event data");
            request.setAttribute("formTitle", title);
            request.setAttribute("formEventDate", eventDateValue);
            request.setAttribute("formMaxSeats", maxSeatsValue);

            showEventsPage(request, response);
        }
    }

    private void showEventsPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("events", eventService.getUpcomingEvents());
        request.getRequestDispatcher(EVENTS_VIEW).forward(request, response);
    }

    private LocalDate parseEventDate(String eventDateValue) {
        if (eventDateValue == null || eventDateValue.isBlank()) {
            throw new ValidationException("Event date is required");
        }

        return LocalDate.parse(eventDateValue);
    }
}