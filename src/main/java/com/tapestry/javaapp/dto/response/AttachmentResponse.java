package com.tapestry.javaapp.dto.response;

import com.tapestry.javaapp.model.Attachment;
import java.time.Instant;

public class AttachmentResponse {

    private Long id;
    private String filename;
    private String fileUrl;
    private Long issueId;
    private UserResponse uploadedBy;
    private Instant uploadedAt;
    private Long fileSize;

    public static AttachmentResponse from(Attachment attachment) {
        AttachmentResponse r = new AttachmentResponse();
        r.setId(attachment.getId());
        r.setFilename(attachment.getFilename());
        r.setFileUrl(attachment.getFileUrl());
        r.setIssueId(attachment.getIssueId());
        r.setUploadedAt(attachment.getUploadedAt());
        r.setFileSize(attachment.getFileSize());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }
    public UserResponse getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(UserResponse uploadedBy) { this.uploadedBy = uploadedBy; }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
}
