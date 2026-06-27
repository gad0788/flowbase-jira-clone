package com.tapestry.javaapp.repository;

import com.tapestry.javaapp.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
