package com.flowbase.jira.service;

import com.flowbase.jira.dto.request.ProjectRequest;
import com.flowbase.jira.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectResponse> findAll();
    ProjectResponse findById(Long id);
    ProjectResponse findByKey(String key);
    ProjectResponse create(ProjectRequest request);
    ProjectResponse update(Long id, ProjectRequest request);
    void archive(Long id);
}
