package com.flowbase.jira.service;

import com.flowbase.jira.dto.request.IssueRequest;
import com.flowbase.jira.dto.request.IssueTransitionRequest;
import com.flowbase.jira.dto.response.IssueResponse;
import com.flowbase.jira.model.*;
import com.flowbase.jira.repository.*;
import com.flowbase.jira.service.impl.IssueServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IssueServiceTest {

    @Mock private IssueRepository issueRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private SprintRepository sprintRepository;
    @Mock private LabelRepository labelRepository;
    @Mock private ComponentRepository componentRepository;
    @Mock private WorkflowTransitionRepository workflowTransitionRepository;
    @Mock private ProjectSequenceRepository projectSequenceRepository;

    private IssueService issueService;
    private Project project;
    private User reporter;
    private Issue issue;

    @BeforeEach
    void setUp() {
        issueService = new IssueServiceImpl(issueRepository, projectRepository,
                userRepository, sprintRepository, labelRepository,
                componentRepository, workflowTransitionRepository, projectSequenceRepository);

        project = new Project("PROJ", "Test Project");
        project.setId(1L);

        reporter = new User("Reporter", "reporter@test.com", "password");
        reporter.setId(1L);

        issue = new Issue();
        issue.setId(1L);
        issue.setKey("PROJ-1");
        issue.setSummary("Test Issue");
        issue.setIssueType("TASK");
        issue.setStatus("BACKLOG");
        issue.setPriority("MEDIUM");
        issue.setProjectId(1L);
        issue.setReporterId(1L);
    }

    @Test
    void findByProjectIdShouldReturnIssues() {
        when(issueRepository.findByProjectId(1L)).thenReturn(List.of(issue));
        when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        List<IssueResponse> result = issueService.findByProjectId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKey()).isEqualTo("PROJ-1");
    }

    @Test
    void findByIdShouldReturnIssue() {
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
        when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        IssueResponse result = issueService.findById(1L);

        assertThat(result.getSummary()).isEqualTo("Test Issue");
        assertThat(result.getKey()).isEqualTo("PROJ-1");
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        when(issueRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> issueService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createShouldGenerateKeyAndSave() {
        IssueRequest request = new IssueRequest();
        request.setSummary("New Issue");
        request.setIssueType(IssueType.STORY);
        request.setReporterId(1L);
        request.setProjectId(1L);

        when(projectRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectSequenceRepository.findByProjectId(1L)).thenReturn(Optional.of(new ProjectSequence(1L, 1L)));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);
        when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));

        IssueResponse result = issueService.create(request);

        assertThat(result.getKey()).isEqualTo("PROJ-1");
    }

    @Test
    void transitionShouldUpdateStatusAndLog() {
        IssueTransitionRequest transition = new IssueTransitionRequest();
        transition.setStatus(IssueStatus.IN_PROGRESS);
        transition.setUserId(1L);

        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
        when(userRepository.findById(1L)).thenReturn(Optional.of(reporter));
        when(issueRepository.save(any(Issue.class))).thenReturn(issue);
        when(workflowTransitionRepository.save(any(WorkflowTransition.class))).thenReturn(null);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        IssueResponse result = issueService.transition(1L, transition);

        assertThat(result.getKey()).isEqualTo("PROJ-1");
    }

    @Test
    void deleteShouldRemoveIssue() {
        when(issueRepository.existsById(1L)).thenReturn(true);

        issueService.delete(1L);

        verify(issueRepository).deleteById(1L);
    }

    @Test
    void deleteShouldThrowWhenNotFound() {
        when(issueRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> issueService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }
}
