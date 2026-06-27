package com.flowbase.jira.service;

import com.flowbase.jira.dto.request.CommentRequest;
import com.flowbase.jira.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> findByIssueId(Long issueId);
    CommentResponse create(Long issueId, CommentRequest request);
    CommentResponse update(Long commentId, CommentRequest request);
    void delete(Long commentId);
}
