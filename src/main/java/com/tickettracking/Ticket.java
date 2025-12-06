package com.tickettracking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a ticket in a ticket tracking system.
 * A ticket contains details such as its title, description, status, priority,
 * creation and update timestamps, assigned user, and associated comments.
 */
public class Ticket {
    private String id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String assignedTo;
    private List<Comment> comments;

    public enum Status {
        NEW, IN_PROGRESS, ON_HOLD, RESOLVED, OPEN, CLOSED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    // Default constructor
    public Ticket() {
        this.title = "";
        this.description = "";
        this.priority = Priority.LOW;
        this.comments = new ArrayList<>();
    }


    // Constructor for new tickets
    public Ticket(String title, String description, Priority priority, LocalDateTime createdAt) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = Status.NEW; // New tickets start with NEW status
        this.createdAt = createdAt;
        this.updatedAt = createdAt; // Initially same as created
        this.assignedTo = null; // Initially unassigned
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {

        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<Comment> getComments() {
        return comments != null ? comments : new ArrayList<>();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        comment.setTicketId(this.id);
        this.comments.add(comment);
    }
}

