package com.tapestry.javaapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import com.tapestry.javaapp.dto.request.IssueRequest;
import com.tapestry.javaapp.dto.request.IssueTransitionRequest;
import com.tapestry.javaapp.dto.response.IssueResponse;
import com.tapestry.javaapp.model.IssueStatus;
import com.tapestry.javaapp.model.IssueType;
import com.tapestry.javaapp.model.Priority;
import com.tapestry.javaapp.service.IssueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IssueController.class)
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IssueService issueService;

    @Autowired
    private ObjectMapper objectMapper;

    private IssueResponse sampleResponse() {
        IssueResponse r = new IssueResponse();
        r.setId(1L);
        r.setKey("PROJ-1");
        r.setSummary("Test Issue");
        r.setIssueType("TASK");
        r.setStatus("BACKLOG");
        r.setPriority("MEDIUM");
        r.setCreatedAt(Instant.now());
        r.setUpdatedAt(Instant.now());

        IssueResponse.ProjectSummary ps = new IssueResponse.ProjectSummary();
        ps.setId(1L);
        ps.setKey("PROJ");
        ps.setName("Project");
        r.setProject(ps);

        return r;
    }

    @Test
    void getIssuesByProjectShouldReturnList() throws Exception {
        when(issueService.findByProjectId(1L)).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/v1/issues/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("PROJ-1"));
    }

    @Test
    void getIssueShouldReturnIssue() throws Exception {
        when(issueService.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/v1/issues/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("PROJ-1"));
    }

    @Test
    void getIssueShouldReturn404() throws Exception {
        when(issueService.findById(99L)).thenThrow(new EntityNotFoundException("Issue not found: 99"));

        mockMvc.perform(get("/api/v1/issues/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getIssueByKeyShouldReturnIssue() throws Exception {
        when(issueService.findByKey("PROJ-1")).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/v1/issues/key/PROJ-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("PROJ-1"));
    }

    @Test
    void createIssueShouldReturnCreated() throws Exception {
        when(issueService.create(any(IssueRequest.class))).thenReturn(sampleResponse());

        IssueRequest request = new IssueRequest();
        request.setSummary("Test Issue");
        request.setIssueType(IssueType.TASK);
        request.setReporterId(1L);
        request.setProjectId(1L);

        mockMvc.perform(post("/api/v1/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.key").value("PROJ-1"));
    }

    @Test
    void createIssueShouldReturn400WhenInvalid() throws Exception {
        IssueRequest request = new IssueRequest();

        mockMvc.perform(post("/api/v1/issues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateIssueShouldReturnUpdated() throws Exception {
        when(issueService.update(eq(1L), any(IssueRequest.class))).thenReturn(sampleResponse());

        IssueRequest request = new IssueRequest();
        request.setSummary("Updated");
        request.setIssueType(IssueType.TASK);
        request.setReporterId(1L);
        request.setProjectId(1L);

        mockMvc.perform(put("/api/v1/issues/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("PROJ-1"));
    }

    @Test
    void transitionIssueShouldReturnUpdated() throws Exception {
        when(issueService.transition(eq(1L), any(IssueTransitionRequest.class))).thenReturn(sampleResponse());

        IssueTransitionRequest request = new IssueTransitionRequest();
        request.setStatus(IssueStatus.IN_PROGRESS);
        request.setUserId(1L);

        mockMvc.perform(post("/api/v1/issues/1/transitions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteIssueShouldReturn204() throws Exception {
        doNothing().when(issueService).delete(1L);

        mockMvc.perform(delete("/api/v1/issues/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteIssueShouldReturn404() throws Exception {
        doThrow(new EntityNotFoundException("Issue not found: 99")).when(issueService).delete(99L);

        mockMvc.perform(delete("/api/v1/issues/99"))
                .andExpect(status().isNotFound());
    }
}
