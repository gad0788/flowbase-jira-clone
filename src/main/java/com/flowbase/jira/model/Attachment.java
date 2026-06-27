package com.flowbase.jira.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filename;
    private String fileUrl;
    @Column(name = "issue_id")
    private Long issueId;
    @Column(name = "uploaded_by")
    private Long uploadedBy;
    private Instant uploadedAt;
    private Long fileSize;

    public Attachment() {}

    public Attachment(String filename, String fileUrl, Long issueId, Long uploadedBy) {
        this.filename = filename;
        this.fileUrl = fileUrl;
        this.issueId = issueId;
        this.uploadedBy = uploadedBy;
    }

    @PrePersist
    protected void onCreate() { uploadedAt = Instant.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }
    public Long getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
}
