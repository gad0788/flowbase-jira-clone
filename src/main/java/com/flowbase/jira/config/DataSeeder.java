package com.flowbase.jira.config;

import com.flowbase.jira.model.Label;
import com.flowbase.jira.model.User;
import com.flowbase.jira.repository.LabelRepository;
import com.flowbase.jira.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, LabelRepository labelRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.passwordEncoder = passwordEncoder;
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
        String defaultPassword = passwordEncoder.encode("password");
        userRepository.save(new User("Admin User", "admin@flowbase.com", defaultPassword));
        userRepository.save(new User("Developer", "dev@flowbase.com", defaultPassword));
        userRepository.save(new User("Product Manager", "pm@flowbase.com", defaultPassword));
        log.info("Seeded {} users with default password", 3);
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
