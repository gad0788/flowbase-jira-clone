package com.tapestry.javaapp.service;

import com.tapestry.javaapp.dto.request.UserRequest;
import com.tapestry.javaapp.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    UserResponse create(UserRequest request);
    UserResponse update(Long id, UserRequest request);
    void deactivate(Long id);
}
