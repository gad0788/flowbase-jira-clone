package com.flowbase.jira.service.impl;

import com.flowbase.jira.dto.request.ComponentRequest;
import com.flowbase.jira.dto.response.ComponentResponse;
import com.flowbase.jira.dto.response.IssueResponse;
import com.flowbase.jira.dto.response.UserResponse;
import com.flowbase.jira.model.Component;
import com.flowbase.jira.repository.ComponentRepository;
import com.flowbase.jira.repository.ProjectRepository;
import com.flowbase.jira.repository.UserRepository;
import com.flowbase.jira.service.ComponentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ComponentServiceImpl implements ComponentService {

    private final ComponentRepository componentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ComponentServiceImpl(ComponentRepository componentRepository,
                                ProjectRepository projectRepository,
                                UserRepository userRepository) {
        this.componentRepository = componentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComponentResponse> findByProjectId(Long projectId) {
        return componentRepository.findByProjectId(projectId)
                .stream().map(this::enrichComponentResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ComponentResponse findById(Long id) {
        return enrichComponentResponse(findComponent(id));
    }

    @Override
    public ComponentResponse create(ComponentRequest request) {
        if (!projectRepository.existsById(request.getProjectId())) {
            throw new EntityNotFoundException("Project not found: " + request.getProjectId());
        }
        Component component = new Component(request.getName(), request.getProjectId());
        component.setDescription(request.getDescription());
        if (request.getLeadId() != null) {
            if (!userRepository.existsById(request.getLeadId())) {
                throw new EntityNotFoundException("User not found: " + request.getLeadId());
            }
            component.setLeadId(request.getLeadId());
        }
        return enrichComponentResponse(componentRepository.save(component));
    }

    @Override
    public ComponentResponse update(Long id, ComponentRequest request) {
        Component component = findComponent(id);
        component.setName(request.getName());
        component.setDescription(request.getDescription());
        if (request.getLeadId() != null) {
            if (!userRepository.existsById(request.getLeadId())) {
                throw new EntityNotFoundException("User not found: " + request.getLeadId());
            }
            component.setLeadId(request.getLeadId());
        } else {
            component.setLeadId(null);
        }
        return enrichComponentResponse(componentRepository.save(component));
    }

    @Override
    public void delete(Long id) {
        if (!componentRepository.existsById(id)) {
            throw new EntityNotFoundException("Component not found: " + id);
        }
        componentRepository.deleteById(id);
    }

    private Component findComponent(Long id) {
        return componentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Component not found: " + id));
    }

    private ComponentResponse enrichComponentResponse(Component component) {
        ComponentResponse r = ComponentResponse.from(component);
        projectRepository.findById(component.getProjectId()).ifPresent(p ->
                r.setProject(IssueResponse.ProjectSummary.from(p)));
        if (component.getLeadId() != null) {
            userRepository.findById(component.getLeadId()).ifPresent(u ->
                    r.setLead(UserResponse.from(u)));
        }
        return r;
    }
}
