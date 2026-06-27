package com.flowbase.jira.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentRequest {

    @NotBlank
    private String body;

    @NotNull
    private Long authorId;

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}
