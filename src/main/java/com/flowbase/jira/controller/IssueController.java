package com.flowbase.jira.controller;

import com.flowbase.jira.dto.request.IssueRequest;
import com.flowbase.jira.dto.request.IssueTransitionRequest;
import com.flowbase.jira.dto.response.IssueResponse;
import com.flowbase.jira.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<IssueResponse>> getIssuesByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(issueService.findByProjectId(projectId));
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<IssueResponse>> getIssuesBySprint(@PathVariable Long sprintId) {
        return ResponseEntity.ok(issueService.findBySprintId(sprintId));
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<List<IssueResponse>> getIssuesByAssignee(@PathVariable Long assigneeId) {
        return ResponseEntity.ok(issueService.findByAssigneeId(assigneeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponse> getIssue(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.findById(id));
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<IssueResponse> getIssueByKey(@PathVariable String key) {
        return ResponseEntity.ok(issueService.findByKey(key));
    }

    @PostMapping
    public ResponseEntity<IssueResponse> createIssue(@Valid @RequestBody IssueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IssueResponse> updateIssue(@PathVariable Long id, @Valid @RequestBody IssueRequest request) {
        return ResponseEntity.ok(issueService.update(id, request));
    }

    @PostMapping("/{id}/transitions")
    public ResponseEntity<IssueResponse> transitionIssue(@PathVariable Long id,
                                                          @Valid @RequestBody IssueTransitionRequest request) {
        return ResponseEntity.ok(issueService.transition(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
