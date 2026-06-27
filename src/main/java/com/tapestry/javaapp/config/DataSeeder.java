package com.tapestry.javaapp.config;

import com.tapestry.javaapp.model.Label;
import com.tapestry.javaapp.model.User;
import com.tapestry.javaapp.repository.LabelRepository;
import com.tapestry.javaapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final LabelRepository labelRepository;

    public DataSeeder(UserRepository userRepository, LabelRepository labelRepository) {
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUsers();
        }
        if (labelRepository.count() == 0) {
            seedLabels();
        }
        log.info("Seed data initialized");
    }

    private void seedUsers() {
        userRepository.save(new User("Admin User", "admin@tapestry.com"));
        userRepository.save(new User("Developer", "dev@tapestry.com"));
        userRepository.save(new User("Product Manager", "pm@tapestry.com"));
        log.info("Seeded {} users", 3);
    }

    private void seedLabels() {
        labelRepository.save(new Label("bug", "#e44d42"));
        labelRepository.save(new Label("enhancement", "#61affe"));
        labelRepository.save(new Label("documentation", "#6b42e4"));
        labelRepository.save(new Label("urgent", "#ff3333"));
        labelRepository.save(new Label("frontend", "#36b37e"));
        labelRepository.save(new Label("backend", "#6554c0"));
        log.info("Seeded {} labels", 6);
    }
}
