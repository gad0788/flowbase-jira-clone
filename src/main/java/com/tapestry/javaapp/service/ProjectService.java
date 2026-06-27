package com.tapestry.javaapp.service;

import com.tapestry.javaapp.dto.request.ProjectRequest;
import com.tapestry.javaapp.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectResponse> findAll();
    ProjectResponse findById(Long id);
    ProjectResponse findByKey(String key);
    ProjectResponse create(ProjectRequest request);
    ProjectResponse update(Long id, ProjectRequest request);
    void archive(Long id);
}
