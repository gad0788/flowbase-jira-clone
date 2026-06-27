package com.tapestry.javaapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_sequences")
public class ProjectSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "project_id")
    private Long projectId;
    private Long currentValue;

    public ProjectSequence() {}

    public ProjectSequence(Long projectId, Long currentValue) {
        this.projectId = projectId;
        this.currentValue = currentValue;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getCurrentValue() { return currentValue; }
    public void setCurrentValue(Long currentValue) { this.currentValue = currentValue; }
}
