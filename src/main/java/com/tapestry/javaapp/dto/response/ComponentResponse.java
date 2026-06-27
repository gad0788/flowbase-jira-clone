package com.tapestry.javaapp.dto.response;

import com.tapestry.javaapp.model.Component;

public class ComponentResponse {

    private Long id;
    private String name;
    private String description;
    private IssueResponse.ProjectSummary project;
    private UserResponse lead;

    public static ComponentResponse from(Component component) {
        ComponentResponse r = new ComponentResponse();
        r.setId(component.getId());
        r.setName(component.getName());
        r.setDescription(component.getDescription());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public IssueResponse.ProjectSummary getProject() { return project; }
    public void setProject(IssueResponse.ProjectSummary project) { this.project = project; }
    public UserResponse getLead() { return lead; }
    public void setLead(UserResponse lead) { this.lead = lead; }
}
