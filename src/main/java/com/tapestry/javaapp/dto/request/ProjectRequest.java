package com.tapestry.javaapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProjectRequest {

    @NotBlank
    @Size(min = 2, max = 10)
    private String key;

    @NotBlank
    private String name;

    private String description;

    private Long leadId;

    private String category;

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
