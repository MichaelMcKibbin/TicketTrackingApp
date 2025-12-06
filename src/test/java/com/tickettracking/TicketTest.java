package com.tickettracking;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void defaultConstructor_setsReasonableDefaults() {
        Ticket ticket = new Ticket();

        assertEquals("", ticket.getTitle());
        assertEquals("", ticket.getDescription());
        assertEquals(Ticket.Priority.LOW, ticket.getPriority());
        assertNotNull(ticket.getComments(), "Comments list should be non-null");
        assertTrue(ticket.getComments().isEmpty(), "Comments list should be empty by default");
    }

    @Test
    void parameterizedConstructor_setsFieldsAndNewStatus() {
        LocalDateTime now = LocalDateTime.now();
        Ticket ticket = new Ticket(
                "Login broken",
                "User cannot log in with valid credentials",
                Ticket.Priority.HIGH,
                now
        );

        assertEquals("Login broken", ticket.getTitle());
        assertEquals("User cannot log in with valid credentials", ticket.getDescription());
        assertEquals(Ticket.Priority.HIGH, ticket.getPriority());
        assertEquals(Ticket.Status.NEW, ticket.getStatus(), "New tickets should start in NEW status");
        assertEquals(now, ticket.getCreatedAt());
        assertEquals(now, ticket.getUpdatedAt());
        assertNull(ticket.getAssignedTo());
    }

    @Test
    void getComments_returnsEmptyListIfNullInternally() {
        Ticket ticket = new Ticket();
        // simulate comments being null internally
        ticket.setComments(null);

        List<Comment> comments = ticket.getComments();
        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }

    @Test
    void addComment_setsTicketIdAndAddsToList() {
        Ticket ticket = new Ticket();
        ticket.setId("42");

        Comment comment = new Comment();
        comment.setContent("Investigating issue");
        comment.setCreatedBy("support1");
        comment.setCreatedAt(LocalDateTime.now());

        ticket.addComment(comment);

        List<Comment> comments = ticket.getComments();
        assertEquals(1, comments.size());
        Comment stored = comments.get(0);

        assertEquals("Investigating issue", stored.getContent());
        assertEquals("support1", stored.getCreatedBy());
        assertEquals("42", stored.getTicketId(), "Comment ticketId should be set by addComment()");
    }
}
