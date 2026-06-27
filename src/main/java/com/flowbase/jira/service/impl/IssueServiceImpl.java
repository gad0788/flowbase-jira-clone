package com.flowbase.jira.service.impl;

import com.flowbase.jira.dto.request.IssueRequest;
import com.flowbase.jira.dto.request.IssueTransitionRequest;
import com.flowbase.jira.dto.response.IssueResponse;
import com.flowbase.jira.model.*;
import com.flowbase.jira.repository.*;
import com.flowbase.jira.service.IssueService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final SprintRepository sprintRepository;
    private final LabelRepository labelRepository;
    private final ComponentRepository componentRepository;
    private final WorkflowTransitionRepository workflowTransitionRepository;
    private final ProjectSequenceRepository projectSequenceRepository;

    public IssueServiceImpl(IssueRepository issueRepository,
                            ProjectRepository projectRepository,
                            UserRepository userRepository,
                            SprintRepository sprintRepository,
                            LabelRepository labelRepository,
                            ComponentRepository componentRepository,
                            WorkflowTransitionRepository workflowTransitionRepository,
                            ProjectSequenceRepository projectSequenceRepository) {
        this.issueRepository = issueRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.sprintRepository = sprintRepository;
        this.labelRepository = labelRepository;
        this.componentRepository = componentRepository;
        this.workflowTransitionRepository = workflowTransitionRepository;
        this.projectSequenceRepository = projectSequenceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> findByProjectId(Long projectId) {
        return issueRepository.findByProjectId(projectId).stream()
                .map(this::enrichIssueResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> findBySprintId(Long sprintId) {
        return issueRepository.findBySprintId(sprintId).stream()
                .map(this::enrichIssueResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueResponse> findByAssigneeId(Long assigneeId) {
        return issueRepository.findByAssigneeId(assigneeId).stream()
                .map(this::enrichIssueResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponse findById(Long id) {
        return enrichIssueResponse(findIssue(id));
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponse findByKey(String key) {
        Issue issue = issueRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found: " + key));
        return enrichIssueResponse(issue);
    }

    @Override
    public IssueResponse create(IssueRequest request) {
        validateProjectExists(request.getProjectId());
        validateUserExists(request.getReporterId());

        Issue issue = new Issue();
        issue.setKey(generateIssueKey(request.getProjectId()));
        issue.setSummary(request.getSummary());
        issue.setDescription(request.getDescription());
        issue.setIssueType(request.getIssueType().name());
        issue.setProjectId(request.getProjectId());
        issue.setReporterId(request.getReporterId());
        issue.setStatus("BACKLOG");

        if (request.getPriority() != null) {
            issue.setPriority(request.getPriority().name());
        }
        if (request.getAssigneeId() != null) {
            validateUserExists(request.getAssigneeId());
            issue.setAssigneeId(request.getAssigneeId());
        }
        if (request.getParentId() != null) {
            issue.setParentId(request.getParentId());
        }
        if (request.getSprintId() != null) {
            issue.setSprintId(request.getSprintId());
        }
        issue.setStoryPoints(request.getStoryPoints());
        issue.setDueDate(request.getDueDate());

        if (request.getLabels() != null) {
            issue.setLabels(resolveLabelNames(request.getLabels()));
        }
        if (request.getComponentIds() != null) {
            issue.setComponentIds(new HashSet<>(request.getComponentIds()));
        }

        Issue saved = issueRepository.save(issue);

        WorkflowTransition transition = new WorkflowTransition(
                saved.getId(), null, "BACKLOG", request.getReporterId());
        workflowTransitionRepository.save(transition);

        return enrichIssueResponse(saved);
    }

    @Override
    public IssueResponse update(Long id, IssueRequest request) {
        Issue issue = findIssue(id);

        issue.setSummary(request.getSummary());
        issue.setDescription(request.getDescription());
        if (request.getIssueType() != null) {
            issue.setIssueType(request.getIssueType().name());
        }
        if (request.getPriority() != null) {
            issue.setPriority(request.getPriority().name());
        }
        if (request.getAssigneeId() != null) {
            validateUserExists(request.getAssigneeId());
            issue.setAssigneeId(request.getAssigneeId());
        } else {
            issue.setAssigneeId(null);
        }
        issue.setStoryPoints(request.getStoryPoints());
        issue.setDueDate(request.getDueDate());

        if (request.getSprintId() != null) {
            issue.setSprintId(request.getSprintId());
        } else {
            issue.setSprintId(null);
        }

        if (request.getLabels() != null) {
            issue.setLabels(resolveLabelNames(request.getLabels()));
        }
        if (request.getComponentIds() != null) {
            issue.setComponentIds(new HashSet<>(request.getComponentIds()));
        }

        return enrichIssueResponse(issueRepository.save(issue));
    }

    @Override
    public IssueResponse transition(Long id, IssueTransitionRequest request) {
        Issue issue = findIssue(id);
        String fromStatus = issue.getStatus();

        issue.setStatus(request.getStatus().name());

        if (request.getStatus() == IssueStatus.DONE || request.getStatus() == IssueStatus.CANCELLED) {
            issue.setResolution(request.getResolution() != null ? request.getResolution().name() : null);
            issue.setResolvedAt(Instant.now());
        } else {
            issue.setResolution(null);
            issue.setResolvedAt(null);
        }

        Issue saved = issueRepository.save(issue);

        WorkflowTransition transition = new WorkflowTransition(
                saved.getId(), fromStatus, request.getStatus().name(), request.getUserId());
        workflowTransitionRepository.save(transition);

        return enrichIssueResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new EntityNotFoundException("Issue not found: " + id);
        }
        issueRepository.deleteById(id);
    }

    private Issue findIssue(Long id) {
        return issueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Issue not found: " + id));
    }

    private String generateIssueKey(Long projectId) {
        String projectKey = projectRepository.findById(projectId)
                .map(Project::getKey)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));
        ProjectSequence seq = projectSequenceRepository.findByProjectId(projectId)
                .orElseGet(() -> projectSequenceRepository.save(new ProjectSequence(projectId, 1L)));
        seq.setCurrentValue(seq.getCurrentValue() + 1);
        projectSequenceRepository.save(seq);
        return projectKey + "-" + seq.getCurrentValue();
    }

    private Set<String> resolveLabelNames(Set<String> labelNames) {
        Set<String> resolved = new HashSet<>();
        for (String name : labelNames) {
            Label label = labelRepository.findByName(name)
                    .orElseGet(() -> {
                        Label l = new Label(name, "#" + Integer.toHexString(name.hashCode()).substring(0, 6));
                        return labelRepository.save(l);
                    });
            resolved.add(label.getName());
        }
        return resolved;
    }

    private IssueResponse enrichIssueResponse(Issue issue) {
        IssueResponse r = IssueResponse.from(issue);
        if (issue.getAssigneeId() != null) {
            userRepository.findById(issue.getAssigneeId()).ifPresent(u ->
                    r.setAssignee(com.flowbase.jira.dto.response.UserResponse.from(u)));
        }
        if (issue.getReporterId() != null) {
            userRepository.findById(issue.getReporterId()).ifPresent(u ->
                    r.setReporter(com.flowbase.jira.dto.response.UserResponse.from(u)));
        }
        if (issue.getProjectId() != null) {
            projectRepository.findById(issue.getProjectId()).ifPresent(p ->
                    r.setProject(IssueResponse.ProjectSummary.from(p)));
        }
        if (issue.getParentId() != null) {
            issueRepository.findById(issue.getParentId()).ifPresent(parent ->
                    r.setParent(IssueResponse.IssueSummary.from(parent)));
        }
        if (issue.getSprintId() != null) {
            sprintRepository.findById(issue.getSprintId()).ifPresent(s ->
                    r.setSprint(IssueResponse.SprintSummary.from(s)));
        }
        if (issue.getComponentIds() != null) {
            r.setComponents(issue.getComponentIds().stream()
                    .map(cid -> componentRepository.findById(cid))
                    .filter(Optional::isPresent)
                    .map(opt -> IssueResponse.ComponentSummary.from(opt.get()))
                    .collect(Collectors.toSet()));
        }
        return r;
    }

    private void validateProjectExists(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException("Project not found: " + id);
        }
    }

    private void validateUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found: " + id);
        }
    }
}
