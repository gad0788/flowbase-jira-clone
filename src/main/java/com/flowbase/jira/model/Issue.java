package com.flowbase.jira.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "issue_key")
    private String key;
    private String summary;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "issue_type")
    private String issueType;
    private String status = "BACKLOG";
    private String priority = "MEDIUM";
    private String resolution;
    @Column(name = "assignee_id")
    private Long assigneeId;
    @Column(name = "reporter_id")
    private Long reporterId;
    @Column(name = "project_id")
    private Long projectId;
    @Column(name = "parent_id")
    private Long parentId;
    @Column(name = "sprint_id")
    private Long sprintId;
    private Integer storyPoints;
    private LocalDate dueDate;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;
    @ElementCollection
    @CollectionTable(name = "issue_labels", joinColumns = @JoinColumn(name = "issue_id"))
    @Column(name = "label")
    private Set<String> labels = new HashSet<>();
    @ElementCollection
    @CollectionTable(name = "issue_components", joinColumns = @JoinColumn(name = "issue_id"))
    @Column(name = "component_id")
    private Set<Long> componentIds = new HashSet<>();

    public Issue() {}

    @PrePersist
    protected void onCreate() { createdAt = Instant.now(); updatedAt = Instant.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = Instant.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }
    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getSprintId() { return sprintId; }
    public void setSprintId(Long sprintId) { this.sprintId = sprintId; }
    public Integer getStoryPoints() { return storyPoints; }
    public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
    public Set<String> getLabels() { return labels; }
    public void setLabels(Set<String> labels) { this.labels = labels; }
    public Set<Long> getComponentIds() { return componentIds; }
    public void setComponentIds(Set<Long> componentIds) { this.componentIds = componentIds; }
}
