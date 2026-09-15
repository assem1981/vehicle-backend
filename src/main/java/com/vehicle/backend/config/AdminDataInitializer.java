package com.vehicle.backend.config;

import com.vehicle.backend.security.oauth2.AuthProvider;
import com.vehicle.backend.modules.user.entity.HostApprovalStatus;
import com.vehicle.backend.modules.user.entity.Role;
import com.vehicle.backend.modules.user.entity.User;
import com.vehicle.backend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<User> existingAdmins = userRepository.findByRole(Role.ROLE_ADMIN);
        if (existingAdmins.isEmpty()) {
            log.info("No system admin user found in database. Seeding default system admin user...");
            User admin = User.builder()
                    .firstName("System")
                    .lastName("Admin")
                    .email("admin@vehicle.com")
                    .password(passwordEncoder.encode("AdminPass123!"))
                    .phoneNumber("+10000000000")
                    .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                    .provider(AuthProvider.LOCAL)
                    .active(true)
                    .hostApprovalStatus(HostApprovalStatus.NONE)
                    .emailUpdates(true)
                    .agreedToTerms(true)
                    .termsAgreedAt(LocalDateTime.now())
                    .build();

            User savedAdmin = userRepository.save(admin);
            log.info("System Admin user seeded successfully with database ID: {}", savedAdmin.getId());
        } else {
            log.info("System admin user exists in database with ID: {}", existingAdmins.get(0).getId());
        }
    }
}
