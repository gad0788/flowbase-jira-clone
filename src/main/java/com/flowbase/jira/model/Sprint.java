package com.flowbase.jira.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sprints")
public class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String goal;
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;
    private boolean active;
    @Column(name = "project_id")
    private Long projectId;
    private Instant createdAt;

    public Sprint() {}

    public Sprint(String name, Long projectId) {
        this.name = name;
        this.projectId = projectId;
    }

    @PrePersist
    protected void onCreate() { createdAt = Instant.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public java.time.LocalDate getStartDate() { return startDate; }
    public void setStartDate(java.time.LocalDate startDate) { this.startDate = startDate; }
    public java.time.LocalDate getEndDate() { return endDate; }
    public void setEndDate(java.time.LocalDate endDate) { this.endDate = endDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
