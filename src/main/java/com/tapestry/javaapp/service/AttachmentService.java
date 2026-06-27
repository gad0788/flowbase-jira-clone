package com.tapestry.javaapp.service;

import com.tapestry.javaapp.dto.response.AttachmentResponse;

import java.util.List;

public interface AttachmentService {
    List<AttachmentResponse> findByIssueId(Long issueId);
    AttachmentResponse findById(Long id);
    AttachmentResponse create(Long issueId, String filename, String fileUrl, Long uploadedBy, Long fileSize);
    void delete(Long id);
}
