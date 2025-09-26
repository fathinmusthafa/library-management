package com.sinaukoding.library.management.config;

import com.sinaukoding.library.management.entity.managementuser.User;
import com.sinaukoding.library.management.model.enums.Role;
import com.sinaukoding.library.management.model.enums.Status;
import com.sinaukoding.library.management.repository.managementuser.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setName("SYSTEM ADMIN");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setEmail("admin@mail.com");
            admin.setRole(Role.ADMIN);
            admin.setStatus(Status.AKTIF);
            userRepository.save(admin);
            System.out.println("Admin berhasil dibuat");
        }

    }
}
