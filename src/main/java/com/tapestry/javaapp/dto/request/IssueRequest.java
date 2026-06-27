package com.tapestry.javaapp.dto.request;

import com.tapestry.javaapp.model.IssueType;
import com.tapestry.javaapp.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

public class IssueRequest {

    @NotBlank
    private String summary;

    private String description;

    @NotNull
    private IssueType issueType;

    private Long assigneeId;

    @NotNull
    private Long reporterId;

    @NotNull
    private Long projectId;

    private Long parentId;

    private Long sprintId;

    private Priority priority;

    private Integer storyPoints;

    private LocalDate dueDate;

    private Set<String> labels;

    private Set<Long> componentIds;

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public IssueType getIssueType() { return issueType; }
    public void setIssueType(IssueType issueType) { this.issueType = issueType; }
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
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Integer getStoryPoints() { return storyPoints; }
    public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Set<String> getLabels() { return labels; }
    public void setLabels(Set<String> labels) { this.labels = labels; }
    public Set<Long> getComponentIds() { return componentIds; }
    public void setComponentIds(Set<Long> componentIds) { this.componentIds = componentIds; }
}
