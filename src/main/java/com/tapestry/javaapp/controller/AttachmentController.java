package com.tapestry.javaapp.controller;

import com.tapestry.javaapp.dto.response.AttachmentResponse;
import com.tapestry.javaapp.service.AttachmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/issues/{issueId}/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public ResponseEntity<List<AttachmentResponse>> getAttachments(@PathVariable Long issueId) {
        return ResponseEntity.ok(attachmentService.findByIssueId(issueId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttachmentResponse> getAttachment(@PathVariable Long id) {
        return ResponseEntity.ok(attachmentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AttachmentResponse> createAttachment(@PathVariable Long issueId,
                                                                @RequestBody Map<String, Object> body) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attachmentService.create(
                        issueId,
                        (String) body.get("filename"),
                        (String) body.get("fileUrl"),
                        Long.valueOf(body.get("uploadedBy").toString()),
                        body.get("fileSize") != null ? Long.valueOf(body.get("fileSize").toString()) : null
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long issueId, @PathVariable Long id) {
        attachmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
