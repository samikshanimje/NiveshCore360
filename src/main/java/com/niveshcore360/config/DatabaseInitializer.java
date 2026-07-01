package com.niveshcore360.config;

import com.niveshcore360.dto.UserDTO;
import com.niveshcore360.entity.Role;
import com.niveshcore360.repository.RoleRepository;
import com.niveshcore360.repository.UserRepository;
import com.niveshcore360.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component executing on application startup to seed security roles and default accounts.
 */
@Component
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthService authService;

    @Autowired
    public DatabaseInitializer(UserRepository userRepository,
                               RoleRepository roleRepository,
                               AuthService authService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authService = authService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Roles if missing
        Role adminRole = seedRoleIfMissing("ROLE_ADMIN");
        Role userRole = seedRoleIfMissing("ROLE_USER");
        seedRoleIfMissing("ROLE_ADVISOR");

        // 2. Seed Default Accounts
        if (userRepository.count() == 0) {
            log.info("Seeding system administrator and user accounts...");

            // Administrator account
            authService.register(UserDTO.builder()
                .username("admin")
                .password("admin123")
                .email("admin@niveshcore360.com")
                .fullName("System Administrator")
                .role("ROLE_ADMIN")
                .build());

            // Client account
            authService.register(UserDTO.builder()
                .username("user")
                .password("user123")
                .email("user@niveshcore360.com")
                .fullName("Samiksha Nimje")
                .role("ROLE_USER")
                .build());

            log.info("Default user accounts successfully seeded.");
        }
    }

    private Role seedRoleIfMissing(String name) {
        return roleRepository.findByName(name)
            .orElseGet(() -> {
                log.info("Creating security role: {}", name);
                return roleRepository.save(Role.builder().name(name).build());
            });
    }
}
