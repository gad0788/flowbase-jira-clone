package com.tapestry.javaapp.repository;

import com.tapestry.javaapp.model.ProjectSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectSequenceRepository extends JpaRepository<ProjectSequence, Long> {
    Optional<ProjectSequence> findByProjectId(Long projectId);
}
