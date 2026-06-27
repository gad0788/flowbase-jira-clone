package com.flowbase.jira.repository;

import com.flowbase.jira.model.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, Long> {
    List<WorkflowTransition> findByIssueId(Long issueId);
}
