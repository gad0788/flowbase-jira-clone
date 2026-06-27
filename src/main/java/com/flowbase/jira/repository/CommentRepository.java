package com.flowbase.jira.repository;

import com.flowbase.jira.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByIssueIdOrderByCreatedAtAsc(Long issueId);
}
