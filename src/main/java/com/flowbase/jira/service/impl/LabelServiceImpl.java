package com.flowbase.jira.service.impl;

import com.flowbase.jira.model.Label;
import com.flowbase.jira.repository.LabelRepository;
import com.flowbase.jira.service.LabelService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    public LabelServiceImpl(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Label> findAll() {
        return labelRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Label findById(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label not found: " + id));
    }

    @Override
    public Label create(String name, String color) {
        return labelRepository.save(new Label(name, color));
    }

    @Override
    public void delete(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new EntityNotFoundException("Label not found: " + id);
        }
        labelRepository.deleteById(id);
    }
}
