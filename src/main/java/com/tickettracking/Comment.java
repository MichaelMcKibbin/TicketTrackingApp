package com.tickettracking;

import java.time.LocalDateTime;

/**
 * Represents a comment on a ticket in the ticket tracking system.
 * This class holds information about the comment, including its content,
 * the author, and its creation timestamp.
 */
public class Comment {
    private String id;
    private String ticketId;
    private String content;
    private String createdBy;
    private LocalDateTime createdAt;

    public Comment() {}

    public Comment(String content, String createdBy) {
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
