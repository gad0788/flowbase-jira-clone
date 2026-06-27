package com.tapestry.javaapp.repository;

import com.tapestry.javaapp.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByKey(String key);
}
