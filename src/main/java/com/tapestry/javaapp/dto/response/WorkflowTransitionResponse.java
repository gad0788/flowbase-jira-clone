package com.tapestry.javaapp.dto.response;

import com.tapestry.javaapp.model.WorkflowTransition;
import java.time.Instant;

public class WorkflowTransitionResponse {

    private Long id;
    private Long issueId;
    private String fromStatus;
    private String toStatus;
    private UserResponse byUser;
    private Instant timestamp;

    public static WorkflowTransitionResponse from(WorkflowTransition wt) {
        WorkflowTransitionResponse r = new WorkflowTransitionResponse();
        r.setId(wt.getId());
        r.setIssueId(wt.getIssueId());
        r.setFromStatus(wt.getFromStatus());
        r.setToStatus(wt.getToStatus());
        r.setTimestamp(wt.getTimestamp());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }
    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }
    public UserResponse getByUser() { return byUser; }
    public void setByUser(UserResponse byUser) { this.byUser = byUser; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
