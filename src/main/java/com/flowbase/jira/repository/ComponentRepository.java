package com.flowbase.jira.repository;

import com.flowbase.jira.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponentRepository extends JpaRepository<Component, Long> {
    List<Component> findByProjectId(Long projectId);
}
