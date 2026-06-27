package com.flowbase.jira.service;

import com.flowbase.jira.dto.request.ComponentRequest;
import com.flowbase.jira.dto.response.ComponentResponse;

import java.util.List;

public interface ComponentService {
    List<ComponentResponse> findByProjectId(Long projectId);
    ComponentResponse findById(Long id);
    ComponentResponse create(ComponentRequest request);
    ComponentResponse update(Long id, ComponentRequest request);
    void delete(Long id);
}
