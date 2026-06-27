package com.tapestry.javaapp.controller;

import com.tapestry.javaapp.dto.request.SprintRequest;
import com.tapestry.javaapp.dto.response.SprintResponse;
import com.tapestry.javaapp.service.SprintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sprints")
public class SprintController {

    private final SprintService sprintService;

    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<SprintResponse>> getSprintsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(sprintService.findByProjectId(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SprintResponse> getSprint(@PathVariable Long id) {
        return ResponseEntity.ok(sprintService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SprintResponse> createSprint(@Valid @RequestBody SprintRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sprintService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SprintResponse> updateSprint(@PathVariable Long id, @Valid @RequestBody SprintRequest request) {
        return ResponseEntity.ok(sprintService.update(id, request));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<SprintResponse> startSprint(@PathVariable Long id) {
        return ResponseEntity.ok(sprintService.start(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<SprintResponse> completeSprint(@PathVariable Long id) {
        return ResponseEntity.ok(sprintService.complete(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSprint(@PathVariable Long id) {
        sprintService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
