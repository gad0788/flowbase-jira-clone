package com.flowbase.jira.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "workflow_transitions")
public class WorkflowTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "issue_id")
    private Long issueId;
    private String fromStatus;
    private String toStatus;
    @Column(name = "by_user_id")
    private Long byUserId;
    private Instant timestamp;

    public WorkflowTransition() {}

    public WorkflowTransition(Long issueId, String fromStatus, String toStatus, Long byUserId) {
        this.issueId = issueId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.byUserId = byUserId;
    }

    @PrePersist
    protected void onCreate() { timestamp = Instant.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }
    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }
    public Long getByUserId() { return byUserId; }
    public void setByUserId(Long byUserId) { this.byUserId = byUserId; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
