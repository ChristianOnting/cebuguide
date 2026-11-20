package com.cebuguide.backend.config;

import com.cebuguide.backend.model.Role;
import com.cebuguide.backend.model.User;
import com.cebuguide.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Creates the administrator account on first start. Only the BCrypt hash is stored. */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String name;
    private final String email;
    private final String password;

    public AdminSeeder(UserRepository users, PasswordEncoder passwordEncoder,
                       @Value("${app.admin.name}") String name,
                       @Value("${app.admin.email}") String email,
                       @Value("${app.admin.password}") String password) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.name = name;
        this.email = email.trim().toLowerCase();
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (users.existsByEmailIgnoreCase(email)) {
            return;
        }
        users.save(new User(name, email, passwordEncoder.encode(password), Role.ADMIN));
        log.info("Administrator account created: {}", email);
    }
}
