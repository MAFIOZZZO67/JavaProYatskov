package org.example.controller;

import org.example.config.DbConnection;
import org.example.exception.DoubleRegistrationException;
import org.example.exception.EventNotFoundException;
import org.example.exception.NoAvailableSeatsException;
import org.example.exception.ValidationException;
import org.example.model.EventDetails;
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

@WebServlet("/event")
public class EventServlet extends HttpServlet {
    private static final String EVENT_VIEW = "/WEB-INF/views/event.jsp";

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
        long eventId = parseEventId(request, response);

        if (eventId <= 0) {
            return;
        }

        showEventPage(eventId, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        long eventId = parseEventId(request, response);

        if (eventId <= 0) {
            return;
        }

        String studentName = request.getParameter("studentName");
        String studentEmail = request.getParameter("studentEmail");

        try {
            eventService.registerParticipant(eventId, studentName, studentEmail);

            response.sendRedirect(request.getContextPath() + "/event?id=" + eventId);
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", "Please provide valid registration data");
            request.setAttribute("studentName", studentName);
            request.setAttribute("studentEmail", studentEmail);

            showEventPage(eventId, request, response);
        } catch (NoAvailableSeatsException e) {
            request.setAttribute("errorMessage", "No seats available for this event");

            showEventPage(eventId, request, response);
        } catch (DoubleRegistrationException e) {
            request.setAttribute("errorMessage", "This email is already registered for this event");
            request.setAttribute("studentName", studentName);
            request.setAttribute("studentEmail", studentEmail);

            showEventPage(eventId, request, response);
        } catch (EventNotFoundException e) {
            showEventPage(eventId, request, response);
        }
    }

    private long parseEventId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idValue = request.getParameter("id");

        if (idValue == null || idValue.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Event id is required");
            return -1;
        }

        try {
            long eventId = Long.parseLong(idValue);

            if (eventId <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Event id must be greater than zero");
                return -1;
            }

            return eventId;
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Event id must be a number");
            return -1;
        }
    }

    private void showEventPage(long eventId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            EventDetails eventDetails = eventService.getEventDetails(eventId);

            request.setAttribute("eventDetails", eventDetails);
            request.getRequestDispatcher(EVENT_VIEW).forward(request, response);
        } catch (EventNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Event not found");
        }
    }
}