package com.flowbase.jira.dto.response;

import com.flowbase.jira.model.Sprint;
import java.time.Instant;
import java.time.LocalDate;

public class SprintResponse {

    private Long id;
    private String name;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private IssueResponse.ProjectSummary project;
    private Instant createdAt;

    public static SprintResponse from(Sprint sprint) {
        SprintResponse r = new SprintResponse();
        r.setId(sprint.getId());
        r.setName(sprint.getName());
        r.setGoal(sprint.getGoal());
        r.setStartDate(sprint.getStartDate());
        r.setEndDate(sprint.getEndDate());
        r.setActive(sprint.isActive());
        r.setCreatedAt(sprint.getCreatedAt());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public IssueResponse.ProjectSummary getProject() { return project; }
    public void setProject(IssueResponse.ProjectSummary project) { this.project = project; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
