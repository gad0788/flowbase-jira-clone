package com.flowbase.jira.service.impl;

import com.flowbase.jira.dto.request.SprintRequest;
import com.flowbase.jira.dto.response.IssueResponse;
import com.flowbase.jira.dto.response.SprintResponse;
import com.flowbase.jira.model.Sprint;
import com.flowbase.jira.repository.ProjectRepository;
import com.flowbase.jira.repository.SprintRepository;
import com.flowbase.jira.service.SprintService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SprintServiceImpl implements SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;

    public SprintServiceImpl(SprintRepository sprintRepository, ProjectRepository projectRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SprintResponse> findByProjectId(Long projectId) {
        return sprintRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                .stream().map(this::enrichSprintResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SprintResponse findById(Long id) {
        return enrichSprintResponse(findSprint(id));
    }

    @Override
    public SprintResponse create(SprintRequest request) {
        if (!projectRepository.existsById(request.getProjectId())) {
            throw new EntityNotFoundException("Project not found: " + request.getProjectId());
        }
        Sprint sprint = new Sprint(request.getName(), request.getProjectId());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        return enrichSprintResponse(sprintRepository.save(sprint));
    }

    @Override
    public SprintResponse update(Long id, SprintRequest request) {
        Sprint sprint = findSprint(id);
        sprint.setName(request.getName());
        sprint.setGoal(request.getGoal());
        sprint.setStartDate(request.getStartDate());
        sprint.setEndDate(request.getEndDate());
        return enrichSprintResponse(sprintRepository.save(sprint));
    }

    @Override
    public SprintResponse start(Long id) {
        Sprint sprint = findSprint(id);
        sprint.setActive(true);
        return enrichSprintResponse(sprintRepository.save(sprint));
    }

    @Override
    public SprintResponse complete(Long id) {
        Sprint sprint = findSprint(id);
        sprint.setActive(false);
        return enrichSprintResponse(sprintRepository.save(sprint));
    }

    @Override
    public void delete(Long id) {
        if (!sprintRepository.existsById(id)) {
            throw new EntityNotFoundException("Sprint not found: " + id);
        }
        sprintRepository.deleteById(id);
    }

    private Sprint findSprint(Long id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found: " + id));
    }

    private SprintResponse enrichSprintResponse(Sprint sprint) {
        SprintResponse r = SprintResponse.from(sprint);
        projectRepository.findById(sprint.getProjectId()).ifPresent(p ->
                r.setProject(IssueResponse.ProjectSummary.from(p)));
        return r;
    }
}
