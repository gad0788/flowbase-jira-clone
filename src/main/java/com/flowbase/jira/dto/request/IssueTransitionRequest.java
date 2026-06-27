package com.flowbase.jira.dto.request;

import com.flowbase.jira.model.IssueStatus;
import com.flowbase.jira.model.Resolution;
import jakarta.validation.constraints.NotNull;

public class IssueTransitionRequest {

    @NotNull
    private IssueStatus status;

    private Resolution resolution;

    @NotNull
    private Long userId;

    public IssueStatus getStatus() { return status; }
    public void setStatus(IssueStatus status) { this.status = status; }
    public Resolution getResolution() { return resolution; }
    public void setResolution(Resolution resolution) { this.resolution = resolution; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
