package com.tapestry.javaapp.service;

import com.tapestry.javaapp.dto.request.ProjectRequest;
import com.tapestry.javaapp.dto.response.ProjectResponse;
import com.tapestry.javaapp.model.Project;
import com.tapestry.javaapp.repository.ProjectRepository;
import com.tapestry.javaapp.repository.UserRepository;
import com.tapestry.javaapp.service.impl.ProjectServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;

    private ProjectService projectService;
    private Project project;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl(projectRepository, userRepository);

        project = new Project("TEST", "Test Project");
        project.setId(1L);
        project.setDescription("A test project");
    }

    @Test
    void findAllShouldReturnProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<ProjectResponse> result = projectService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKey()).isEqualTo("TEST");
    }

    @Test
    void findByIdShouldReturnProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectResponse result = projectService.findById(1L);

        assertThat(result.getName()).isEqualTo("Test Project");
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findByKeyShouldReturnProject() {
        when(projectRepository.findByKey("TEST")).thenReturn(Optional.of(project));

        ProjectResponse result = projectService.findByKey("TEST");

        assertThat(result.getKey()).isEqualTo("TEST");
    }

    @Test
    void createShouldSaveProject() {
        ProjectRequest request = new ProjectRequest();
        request.setKey("NEW");
        request.setName("New Project");

        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponse result = projectService.create(request);

        assertThat(result.getKey()).isEqualTo("TEST");
    }

    @Test
    void archiveShouldSetArchived() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        projectService.archive(1L);

        assertThat(project.isArchived()).isTrue();
    }
}
