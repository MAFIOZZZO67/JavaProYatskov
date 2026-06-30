<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${eventDetails.event.title}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body>
<main>
    <p>
        <a class="button" href="${pageContext.request.contextPath}/events">Back to events</a>
    </p>

    <h1><c:out value="${eventDetails.event.title}"/></h1>

    <section>
        <h2>Event Details</h2>

        <dl>
            <dt>Date</dt>
            <dd><c:out value="${eventDetails.event.eventDate}"/></dd>

            <dt>Max seats</dt>
            <dd><c:out value="${eventDetails.event.maxSeats}"/></dd>

            <dt>Available seats</dt>
            <dd><c:out value="${eventDetails.availableSeats}"/></dd>
        </dl>
    </section>

    <section>
        <h2>Register Student</h2>

        <c:if test="${not empty errorMessage}">
            <p role="alert"><c:out value="${errorMessage}"/></p>
        </c:if>

        <c:choose>
            <c:when test="${eventDetails.hasAvailableSeats()}">
                <form method="post" action="${pageContext.request.contextPath}/event?id=${eventDetails.event.id}">
                    <div>
                        <label for="studentName">Student name</label>
                        <input
                                type="text"
                                id="studentName"
                                name="studentName"
                                value="${fn:escapeXml(studentName)}"
                                required>
                    </div>

                    <div>
                        <label for="studentEmail">Student email</label>
                        <input
                                type="email"
                                id="studentEmail"
                                name="studentEmail"
                                value="${fn:escapeXml(studentEmail)}"
                                required>
                    </div>

                    <button type="submit">Register</button>
                </form>
            </c:when>
            <c:otherwise>
                <p>No seats available for this event.</p>
            </c:otherwise>
        </c:choose>
    </section>

    <section>
        <h2>Registered Students</h2>

        <c:choose>
            <c:when test="${empty eventDetails.participants}">
                <p>No students registered yet.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="participant" items="${eventDetails.participants}">
                        <tr>
                            <td><c:out value="${participant.studentName}"/></td>
                            <td><c:out value="${participant.studentEmail}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </section>
</main>
</body>
</html>