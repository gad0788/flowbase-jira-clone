package com.flowbase.jira.service;

import com.flowbase.jira.model.Label;

import java.util.List;

public interface LabelService {
    List<Label> findAll();
    Label findById(Long id);
    Label create(String name, String color);
    void delete(Long id);
}
