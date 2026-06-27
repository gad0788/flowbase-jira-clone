package com.tapestry.javaapp.controller;

import com.tapestry.javaapp.dto.request.CommentRequest;
import com.tapestry.javaapp.dto.response.CommentResponse;
import com.tapestry.javaapp.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/issues/{issueId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long issueId) {
        return ResponseEntity.ok(commentService.findByIssueId(issueId));
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long issueId,
                                                          @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(issueId, request));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long issueId,
                                                          @PathVariable Long commentId,
                                                          @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.update(commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long issueId,
                                              @PathVariable Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
