package com.flowbase.jira.dto.response;

import com.flowbase.jira.model.Issue;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

public class IssueResponse {

    private Long id;
    private String key;
    private String summary;
    private String description;
    private String issueType;
    private String status;
    private String priority;
    private String resolution;
    private UserResponse assignee;
    private UserResponse reporter;
    private ProjectSummary project;
    private IssueSummary parent;
    private SprintSummary sprint;
    private Integer storyPoints;
    private LocalDate dueDate;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;
    private Set<String> labels;
    private Set<ComponentSummary> components;

    public static IssueResponse from(Issue issue) {
        IssueResponse r = new IssueResponse();
        r.setId(issue.getId());
        r.setKey(issue.getKey());
        r.setSummary(issue.getSummary());
        r.setDescription(issue.getDescription());
        r.setIssueType(issue.getIssueType());
        r.setStatus(issue.getStatus());
        r.setPriority(issue.getPriority());
        r.setResolution(issue.getResolution());
        r.setStoryPoints(issue.getStoryPoints());
        r.setDueDate(issue.getDueDate());
        r.setCreatedAt(issue.getCreatedAt());
        r.setUpdatedAt(issue.getUpdatedAt());
        r.setResolvedAt(issue.getResolvedAt());
        r.setLabels(issue.getLabels());
        return r;
    }

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
    public UserResponse getAssignee() { return assignee; }
    public void setAssignee(UserResponse assignee) { this.assignee = assignee; }
    public UserResponse getReporter() { return reporter; }
    public void setReporter(UserResponse reporter) { this.reporter = reporter; }
    public ProjectSummary getProject() { return project; }
    public void setProject(ProjectSummary project) { this.project = project; }
    public IssueSummary getParent() { return parent; }
    public void setParent(IssueSummary parent) { this.parent = parent; }
    public SprintSummary getSprint() { return sprint; }
    public void setSprint(SprintSummary sprint) { this.sprint = sprint; }
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
    public Set<ComponentSummary> getComponents() { return components; }
    public void setComponents(Set<ComponentSummary> components) { this.components = components; }

    public static class ProjectSummary {
        private Long id;
        private String key;
        private String name;

        public static ProjectSummary from(com.flowbase.jira.model.Project p) {
            ProjectSummary s = new ProjectSummary();
            s.setId(p.getId());
            s.setKey(p.getKey());
            s.setName(p.getName());
            return s;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class IssueSummary {
        private Long id;
        private String key;
        private String summary;

        public static IssueSummary from(Issue i) {
            IssueSummary s = new IssueSummary();
            s.setId(i.getId());
            s.setKey(i.getKey());
            s.setSummary(i.getSummary());
            return s;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
    }

    public static class SprintSummary {
        private Long id;
        private String name;

        public static SprintSummary from(com.flowbase.jira.model.Sprint s) {
            SprintSummary ss = new SprintSummary();
            ss.setId(s.getId());
            ss.setName(s.getName());
            return ss;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class ComponentSummary {
        private Long id;
        private String name;

        public static ComponentSummary from(com.flowbase.jira.model.Component c) {
            ComponentSummary cs = new ComponentSummary();
            cs.setId(c.getId());
            cs.setName(c.getName());
            return cs;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
