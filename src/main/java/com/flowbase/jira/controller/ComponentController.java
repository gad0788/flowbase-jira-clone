package com.flowbase.jira.controller;

import com.flowbase.jira.dto.request.ComponentRequest;
import com.flowbase.jira.dto.response.ComponentResponse;
import com.flowbase.jira.service.ComponentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/components")
public class ComponentController {

    private final ComponentService componentService;

    public ComponentController(ComponentService componentService) {
        this.componentService = componentService;
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ComponentResponse>> getComponentsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(componentService.findByProjectId(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComponentResponse> getComponent(@PathVariable Long id) {
        return ResponseEntity.ok(componentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ComponentResponse> createComponent(@Valid @RequestBody ComponentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(componentService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComponentResponse> updateComponent(@PathVariable Long id,
                                                              @Valid @RequestBody ComponentRequest request) {
        return ResponseEntity.ok(componentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComponent(@PathVariable Long id) {
        componentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
