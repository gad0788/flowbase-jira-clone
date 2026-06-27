package com.flowbase.jira.repository;

import com.flowbase.jira.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
