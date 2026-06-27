package com.tapestry.javaapp.repository;

import com.tapestry.javaapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
