package com.tapestry.javaapp.service;

import com.tapestry.javaapp.dto.request.ComponentRequest;
import com.tapestry.javaapp.dto.response.ComponentResponse;

import java.util.List;

public interface ComponentService {
    List<ComponentResponse> findByProjectId(Long projectId);
    ComponentResponse findById(Long id);
    ComponentResponse create(ComponentRequest request);
    ComponentResponse update(Long id, ComponentRequest request);
    void delete(Long id);
}
