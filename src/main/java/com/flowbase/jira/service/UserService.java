package com.flowbase.jira.service;

import com.flowbase.jira.dto.request.UserRequest;
import com.flowbase.jira.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    UserResponse create(UserRequest request);
    UserResponse update(Long id, UserRequest request);
    void deactivate(Long id);
}
