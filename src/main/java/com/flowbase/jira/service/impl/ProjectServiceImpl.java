package com.flowbase.jira.service.impl;

import com.flowbase.jira.dto.request.ProjectRequest;
import com.flowbase.jira.dto.response.ProjectResponse;
import com.flowbase.jira.model.Project;
import com.flowbase.jira.repository.ProjectRepository;
import com.flowbase.jira.repository.UserRepository;
import com.flowbase.jira.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream().map(this::enrichProjectResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return enrichProjectResponse(findProject(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse findByKey(String key) {
        Project project = projectRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + key));
        return enrichProjectResponse(project);
    }

    @Override
    public ProjectResponse create(ProjectRequest request) {
        Project project = new Project(request.getKey().toUpperCase(), request.getName());
        project.setDescription(request.getDescription());
        project.setCategory(request.getCategory());
        if (request.getLeadId() != null) {
            if (!userRepository.existsById(request.getLeadId())) {
                throw new EntityNotFoundException("User not found: " + request.getLeadId());
            }
            project.setLeadId(request.getLeadId());
        }
        return enrichProjectResponse(projectRepository.save(project));
    }

    @Override
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = findProject(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setCategory(request.getCategory());
        if (request.getLeadId() != null) {
            if (!userRepository.existsById(request.getLeadId())) {
                throw new EntityNotFoundException("User not found: " + request.getLeadId());
            }
            project.setLeadId(request.getLeadId());
        } else {
            project.setLeadId(null);
        }
        return enrichProjectResponse(projectRepository.save(project));
    }

    @Override
    public void archive(Long id) {
        Project project = findProject(id);
        project.setArchived(true);
        projectRepository.save(project);
    }

    private Project findProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + id));
    }

    private ProjectResponse enrichProjectResponse(Project project) {
        ProjectResponse r = ProjectResponse.from(project);
        if (project.getLeadId() != null) {
            userRepository.findById(project.getLeadId()).ifPresent(u ->
                    r.setLead(com.flowbase.jira.dto.response.UserResponse.from(u)));
        }
        return r;
    }
}
