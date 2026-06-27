package com.flowbase.jira.repository;

import com.flowbase.jira.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByProjectId(Long projectId);
    List<Issue> findByAssigneeId(Long assigneeId);
    List<Issue> findBySprintId(Long sprintId);
    List<Issue> findByParentId(Long parentId);
    List<Issue> findByProjectIdAndStatus(Long projectId, String status);
    Optional<Issue> findByKey(String key);
}
