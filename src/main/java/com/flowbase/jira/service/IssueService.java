package com.flowbase.jira.service;

import com.flowbase.jira.dto.request.IssueRequest;
import com.flowbase.jira.dto.request.IssueTransitionRequest;
import com.flowbase.jira.dto.response.IssueResponse;

import java.util.List;

public interface IssueService {
    List<IssueResponse> findByProjectId(Long projectId);
    List<IssueResponse> findBySprintId(Long sprintId);
    List<IssueResponse> findByAssigneeId(Long assigneeId);
    IssueResponse findById(Long id);
    IssueResponse findByKey(String key);
    IssueResponse create(IssueRequest request);
    IssueResponse update(Long id, IssueRequest request);
    IssueResponse transition(Long id, IssueTransitionRequest request);
    void delete(Long id);
}
