package com.tapestry.javaapp.service.impl;

import com.tapestry.javaapp.dto.request.UserRequest;
import com.tapestry.javaapp.dto.response.UserResponse;
import com.tapestry.javaapp.model.User;
import com.tapestry.javaapp.repository.UserRepository;
import com.tapestry.javaapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserResponse.from(findUser(id));
    }

    @Override
    public UserResponse create(UserRequest request) {
        User user = new User(request.getDisplayName(), request.getEmailAddress());
        user.setAvatarUrl(request.getAvatarUrl());
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User user = findUser(id);
        user.setDisplayName(request.getDisplayName());
        user.setEmailAddress(request.getEmailAddress());
        user.setAvatarUrl(request.getAvatarUrl());
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    public void deactivate(Long id) {
        User user = findUser(id);
        user.setActive(false);
        userRepository.save(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }
}
