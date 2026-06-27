package com.flowbase.jira.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String body;
    @Column(name = "author_id")
    private Long authorId;
    @Column(name = "issue_id")
    private Long issueId;
    private Instant createdAt;
    private Instant updatedAt;

    public Comment() {}

    public Comment(String body, Long authorId, Long issueId) {
        this.body = body;
        this.authorId = authorId;
        this.issueId = issueId;
    }

    @PrePersist
    protected void onCreate() { createdAt = Instant.now(); updatedAt = Instant.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
