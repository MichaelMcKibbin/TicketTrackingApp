package com.tickettracking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketServiceTest {

    @TempDir
    Path tempDir;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        // Make FILE_PATH = "tickets.json" land in a temp directory
        System.setProperty("user.dir", tempDir.toString());
        ticketService = new TicketService();
    }

    @Test
    void getAllTickets_initiallyEmpty() {
        List<Ticket> tickets = ticketService.getAllTickets();
        assertNotNull(tickets);
        assertTrue(tickets.isEmpty(), "Expected no tickets on first load");
    }

    @Test
    void saveTicket_assignsIdAndTimestamps() {
        Ticket ticket = new Ticket();
        ticket.setTitle("Login issue");
        ticket.setDescription("User cannot log in");
        ticket.setStatus(Ticket.Status.NEW);
        ticket.setPriority(Ticket.Priority.MEDIUM);
        ticket.setAssignedTo("support1");

        ticketService.saveTicket(ticket);

        List<Ticket> all = ticketService.getAllTickets();
        assertEquals(1, all.size());

        Ticket stored = all.get(0);
        assertNotNull(stored.getId(), "ID should be generated");
        assertEquals("Login issue", stored.getTitle());
        assertNotNull(stored.getCreatedAt(), "createdAt should be set");
        // updatedAt may or may not be set depending on your implementation
    }

    @Test
    void saveTicket_generatesIncrementingNumericIds() {
        Ticket t1 = new Ticket();
        t1.setTitle("First");
        t1.setDescription("First ticket");
        t1.setStatus(Ticket.Status.NEW);
        t1.setPriority(Ticket.Priority.LOW);

        Ticket t2 = new Ticket();
        t2.setTitle("Second");
        t2.setDescription("Second ticket");
        t2.setStatus(Ticket.Status.NEW);
        t2.setPriority(Ticket.Priority.HIGH);

        ticketService.saveTicket(t1);
        ticketService.saveTicket(t2);

        List<Ticket> all = ticketService.getAllTickets();
        assertEquals(2, all.size());

        // IDs should be numeric and increasing
        int id1 = Integer.parseInt(all.get(0).getId());
        int id2 = Integer.parseInt(all.get(1).getId());

        assertEquals(id1 + 1, id2);
    }

    @Test
    void updateTicket_updatesExistingTicket() {
        // Create and save
        Ticket t = new Ticket();
        t.setTitle("Original title");
        t.setDescription("Original description");
        t.setStatus(Ticket.Status.NEW);
        t.setPriority(Ticket.Priority.MEDIUM);

        ticketService.saveTicket(t);
        Ticket stored = ticketService.getAllTickets().get(0);
        String id = stored.getId();

        // Modify and update
        stored.setTitle("Updated title");
        stored.setDescription("Updated description");
        stored.setStatus(Ticket.Status.IN_PROGRESS);

        ticketService.updateTicket(stored);

        List<Ticket> all = ticketService.getAllTickets();
        assertEquals(1, all.size());

        Ticket updated = all.get(0);
        assertEquals(id, updated.getId(), "ID should stay the same");
        assertEquals("Updated title", updated.getTitle());
        assertEquals("Updated description", updated.getDescription());
        assertEquals(Ticket.Status.IN_PROGRESS, updated.getStatus());
        assertNotNull(updated.getUpdatedAt(), "updatedAt should be set on update");
    }

    @Test
    void deleteTicket_removesTicket() {
        Ticket t = new Ticket();
        t.setTitle("To be deleted");
        t.setDescription("Delete me");
        t.setStatus(Ticket.Status.NEW);
        t.setPriority(Ticket.Priority.LOW);

        ticketService.saveTicket(t);
        assertEquals(1, ticketService.getAllTickets().size());

        Ticket stored = ticketService.getAllTickets().get(0);
        ticketService.deleteTicket(stored);

        assertTrue(ticketService.getAllTickets().isEmpty(), "Ticket should be removed");
    }

    @Test
    void saveTicket_missingTitle_throwsException() {
        Ticket t = new Ticket();
        t.setDescription("No title");
        t.setStatus(Ticket.Status.NEW);
        t.setPriority(Ticket.Priority.LOW);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> ticketService.saveTicket(t)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("title"),
                "Expected message to mention title");
    }

    @Test
    void saveTicket_missingStatus_throwsException() {
        Ticket t = new Ticket();
        t.setTitle("No status");
        t.setDescription("Missing status");
        t.setPriority(Ticket.Priority.LOW);

        assertThrows(IllegalArgumentException.class, () -> ticketService.saveTicket(t));
    }

    @Test
    void saveTicket_missingPriority_throwsException() {
        Ticket t = new Ticket();
        t.setTitle("No priority");
        t.setDescription("Missing priority");
        t.setStatus(Ticket.Status.NEW);

        assertThrows(IllegalArgumentException.class, () -> ticketService.saveTicket(t));
    }
}
