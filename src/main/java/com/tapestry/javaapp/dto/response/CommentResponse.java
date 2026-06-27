package com.tapestry.javaapp.dto.response;

import com.tapestry.javaapp.model.Comment;
import java.time.Instant;

public class CommentResponse {

    private Long id;
    private String body;
    private UserResponse author;
    private Long issueId;
    private Instant createdAt;
    private Instant updatedAt;

    public static CommentResponse from(Comment comment) {
        CommentResponse r = new CommentResponse();
        r.setId(comment.getId());
        r.setBody(comment.getBody());
        r.setIssueId(comment.getIssueId());
        r.setCreatedAt(comment.getCreatedAt());
        r.setUpdatedAt(comment.getUpdatedAt());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public UserResponse getAuthor() { return author; }
    public void setAuthor(UserResponse author) { this.author = author; }
    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
