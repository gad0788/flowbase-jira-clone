package com.flowbase.jira.service.impl;

import com.flowbase.jira.dto.response.AttachmentResponse;
import com.flowbase.jira.dto.response.UserResponse;
import com.flowbase.jira.model.Attachment;
import com.flowbase.jira.repository.AttachmentRepository;
import com.flowbase.jira.repository.IssueRepository;
import com.flowbase.jira.repository.UserRepository;
import com.flowbase.jira.service.AttachmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository,
                                 IssueRepository issueRepository,
                                 UserRepository userRepository) {
        this.attachmentRepository = attachmentRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentResponse> findByIssueId(Long issueId) {
        return attachmentRepository.findByIssueId(issueId)
                .stream().map(this::enrichAttachmentResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttachmentResponse findById(Long id) {
        return enrichAttachmentResponse(attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found: " + id)));
    }

    @Override
    public AttachmentResponse create(Long issueId, String filename, String fileUrl, Long uploadedBy, Long fileSize) {
        if (!issueRepository.existsById(issueId)) {
            throw new EntityNotFoundException("Issue not found: " + issueId);
        }
        if (!userRepository.existsById(uploadedBy)) {
            throw new EntityNotFoundException("User not found: " + uploadedBy);
        }
        Attachment attachment = new Attachment(filename, fileUrl, issueId, uploadedBy);
        attachment.setFileSize(fileSize);
        return enrichAttachmentResponse(attachmentRepository.save(attachment));
    }

    @Override
    public void delete(Long id) {
        if (!attachmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Attachment not found: " + id);
        }
        attachmentRepository.deleteById(id);
    }

    private AttachmentResponse enrichAttachmentResponse(Attachment attachment) {
        AttachmentResponse r = AttachmentResponse.from(attachment);
        userRepository.findById(attachment.getUploadedBy()).ifPresent(u ->
                r.setUploadedBy(UserResponse.from(u)));
        return r;
    }
}
