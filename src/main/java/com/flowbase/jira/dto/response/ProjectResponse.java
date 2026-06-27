package com.flowbase.jira.dto.response;

import com.flowbase.jira.model.Project;
import java.time.Instant;

public class ProjectResponse {

    private Long id;
    private String key;
    private String name;
    private String description;
    private UserResponse lead;
    private String category;
    private boolean archived;
    private Instant createdAt;
    private Instant updatedAt;

    public static ProjectResponse from(Project project) {
        ProjectResponse r = new ProjectResponse();
        r.setId(project.getId());
        r.setKey(project.getKey());
        r.setName(project.getName());
        r.setDescription(project.getDescription());
        r.setCategory(project.getCategory());
        r.setArchived(project.isArchived());
        r.setCreatedAt(project.getCreatedAt());
        r.setUpdatedAt(project.getUpdatedAt());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public UserResponse getLead() { return lead; }
    public void setLead(UserResponse lead) { this.lead = lead; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
