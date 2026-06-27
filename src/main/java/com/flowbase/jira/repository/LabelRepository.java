package com.flowbase.jira.repository;

import com.flowbase.jira.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Optional<Label> findByName(String name);
}
