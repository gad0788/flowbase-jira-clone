package com.tapestry.javaapp.service;

import com.tapestry.javaapp.dto.request.CommentRequest;
import com.tapestry.javaapp.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> findByIssueId(Long issueId);
    CommentResponse create(Long issueId, CommentRequest request);
    CommentResponse update(Long commentId, CommentRequest request);
    void delete(Long commentId);
}
