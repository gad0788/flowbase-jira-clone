package com.flowbase.jira.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import com.flowbase.jira.dto.request.ProjectRequest;
import com.flowbase.jira.dto.response.ProjectResponse;
import com.flowbase.jira.security.JwtAuthenticationFilter;
import com.flowbase.jira.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectResponse sampleResponse() {
        ProjectResponse r = new ProjectResponse();
        r.setId(1L);
        r.setKey("PROJ");
        r.setName("Test Project");
        r.setDescription("A test project");
        r.setArchived(false);
        r.setCreatedAt(Instant.now());
        r.setUpdatedAt(Instant.now());
        return r;
    }

    @Test
    void getAllProjectsShouldReturnList() throws Exception {
        when(projectService.findAll()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("PROJ"));
    }

    @Test
    void getProjectShouldReturnProject() throws Exception {
        when(projectService.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("PROJ"));
    }

    @Test
    void getProjectShouldReturn404() throws Exception {
        when(projectService.findById(99L)).thenThrow(new EntityNotFoundException("Project not found: 99"));

        mockMvc.perform(get("/api/v1/projects/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProjectByKeyShouldReturnProject() throws Exception {
        when(projectService.findByKey("PROJ")).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/v1/projects/key/PROJ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("PROJ"));
    }

    @Test
    void createProjectShouldReturnCreated() throws Exception {
        when(projectService.create(any(ProjectRequest.class))).thenReturn(sampleResponse());

        ProjectRequest request = new ProjectRequest();
        request.setKey("PROJ");
        request.setName("Test Project");

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.key").value("PROJ"));
    }

    @Test
    void createProjectShouldReturn400WhenInvalid() throws Exception {
        ProjectRequest request = new ProjectRequest();

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProjectShouldReturnUpdated() throws Exception {
        when(projectService.update(eq(1L), any(ProjectRequest.class))).thenReturn(sampleResponse());

        ProjectRequest request = new ProjectRequest();
        request.setKey("PROJ");
        request.setName("Updated");

        mockMvc.perform(put("/api/v1/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("PROJ"));
    }
}
