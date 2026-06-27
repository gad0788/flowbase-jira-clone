package com.flowbase.jira.repository;

import com.flowbase.jira.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    java.util.Optional<User> findByEmailAddress(String emailAddress);
    boolean existsByEmailAddress(String emailAddress);
}
