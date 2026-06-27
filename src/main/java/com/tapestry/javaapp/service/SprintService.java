package com.tapestry.javaapp.service;

import com.tapestry.javaapp.dto.request.SprintRequest;
import com.tapestry.javaapp.dto.response.SprintResponse;

import java.util.List;

public interface SprintService {
    List<SprintResponse> findByProjectId(Long projectId);
    SprintResponse findById(Long id);
    SprintResponse create(SprintRequest request);
    SprintResponse update(Long id, SprintRequest request);
    SprintResponse start(Long id);
    SprintResponse complete(Long id);
    void delete(Long id);
}
