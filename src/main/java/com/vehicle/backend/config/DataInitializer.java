package com.vehicle.backend.config;

import com.vehicle.backend.modules.user.entity.Role;
import com.vehicle.backend.modules.user.entity.User;
import com.vehicle.backend.modules.user.repository.UserRepository;
import com.vehicle.backend.modules.vehicle.entity.Vehicle;
import com.vehicle.backend.modules.vehicle.entity.VehicleStatus;
import com.vehicle.backend.modules.vehicle.repository.VehicleRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.vehicle.backend.modules.host.entity.HostProfile;
import com.vehicle.backend.modules.host.repository.HostRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final HostRepository hostRepository;
    private final VehicleRepository vehicleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.admin.email:admin@vehicle.com}")
    private String adminEmail;

    @Value("${app.admin.password:AdminPassword123!}")
    private String adminPassword;

    @Value("${app.admin.first-name:System}")
    private String adminFirstName;

    @Value("${app.admin.last-name:Admin}")
    private String adminLastName;

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE vehicles MODIFY COLUMN status VARCHAR(50)");
            log.info("Ensured vehicles table status column is VARCHAR(50).");
        } catch (Exception e) {
            log.debug("Notice on altering vehicles status column: {}", e.getMessage());
        }

        User adminUser;
        if (!userRepository.existsByEmail(adminEmail)) {
            log.info("No admin user found with email {}. Creating default admin user...", adminEmail);

            adminUser = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .firstName(adminFirstName)
                    .lastName(adminLastName)
                    .phoneNumber("+10000000000")
                    .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER, Role.ROLE_HOST))
                    .active(true)
                    .build();

            adminUser = userRepository.save(adminUser);
            log.info("Default admin user created successfully with email: {} and roles: {}", adminEmail, adminUser.getRoles());
        } else {
            adminUser = userRepository.findByEmail(adminEmail).orElse(null);
            log.info("Default admin user already exists ({})", adminEmail);
        }

        if (adminUser != null && hostRepository.findByUserId(adminUser.getId()).isEmpty()) {
            HostProfile adminHostProfile = HostProfile.builder()
                    .userId(adminUser.getId())
                    .bio("Executive Fleet Partner & System Master Host")
                    .payoutAccountDetails("Bank Transfer - Verified Account")
                    .averageRating(4.9)
                    .totalTripsCount(18)
                    .totalEarnings(new BigDecimal("2450.00"))
                    .superHost(true)
                    .build();
            hostRepository.save(adminHostProfile);
            log.info("Default HostProfile created for host user ID {}.", adminUser.getId());
        }

        Long hostId = adminUser != null ? adminUser.getId() : 1L;

        if (vehicleRepository.count() == 0) {
            log.info("No vehicles found in database. Seeding initial available vehicles...");

            List<Vehicle> initialVehicles = List.of(
                Vehicle.builder()
                    .hostId(hostId)
                    .make("Toyota")
                    .model("Camry")
                    .year(2023)
                    .vin("1FA6P8CF0H5100001")
                    .category("Sedan")
                    .transmission("Automatic")
                    .seatingCapacity(5)
                    .fuelType("Gasoline")
                    .dailyRate(new BigDecimal("65.00"))
                    .locationCity("New York")
                    .latitude(40.7128)
                    .longitude(-74.0060)
                    .status(VehicleStatus.AVAILABLE)
                    .imageUrl("https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?auto=format&fit=crop&w=800&q=80")
                    .build(),

                Vehicle.builder()
                    .hostId(hostId)
                    .make("Honda")
                    .model("Civic")
                    .year(2024)
                    .vin("1FA6P8CF0H5100002")
                    .category("Sedan")
                    .transmission("Automatic")
                    .seatingCapacity(5)
                    .fuelType("Gasoline")
                    .dailyRate(new BigDecimal("55.00"))
                    .locationCity("Los Angeles")
                    .latitude(34.0522)
                    .longitude(-118.2437)
                    .status(VehicleStatus.AVAILABLE)
                    .imageUrl("https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?auto=format&fit=crop&w=800&q=80")
                    .build(),

                Vehicle.builder()
                    .hostId(hostId)
                    .make("BMW")
                    .model("3 Series")
                    .year(2023)
                    .vin("1FA6P8CF0H5100003")
                    .category("Sedan")
                    .transmission("Automatic")
                    .seatingCapacity(5)
                    .fuelType("Gasoline")
                    .dailyRate(new BigDecimal("110.00"))
                    .locationCity("Miami")
                    .latitude(25.7617)
                    .longitude(-80.1918)
                    .status(VehicleStatus.AVAILABLE)
                    .imageUrl("https://images.unsplash.com/photo-1555215695-3004980ad54e?auto=format&fit=crop&w=800&q=80")
                    .build(),

                Vehicle.builder()
                    .hostId(hostId)
                    .make("Tesla")
                    .model("Model 3")
                    .year(2024)
                    .vin("1FA6P8CF0H5100004")
                    .category("Electric")
                    .transmission("Automatic")
                    .seatingCapacity(5)
                    .fuelType("Electric")
                    .dailyRate(new BigDecimal("95.00"))
                    .locationCity("San Francisco")
                    .latitude(37.7749)
                    .longitude(-122.4194)
                    .status(VehicleStatus.AVAILABLE)
                    .imageUrl("https://images.unsplash.com/photo-1560958089-b8a1929cea89?auto=format&fit=crop&w=800&q=80")
                    .build(),

                Vehicle.builder()
                    .hostId(hostId)
                    .make("Ford")
                    .model("Explorer")
                    .year(2023)
                    .vin("1FA6P8CF0H5100005")
                    .category("SUV")
                    .transmission("Automatic")
                    .seatingCapacity(7)
                    .fuelType("Gasoline")
                    .dailyRate(new BigDecimal("85.00"))
                    .locationCity("Chicago")
                    .latitude(41.8781)
                    .longitude(-87.6298)
                    .status(VehicleStatus.AVAILABLE)
                    .imageUrl("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=800&q=80")
                    .build(),

                Vehicle.builder()
                    .hostId(hostId)
                    .make("Porsche")
                    .model("911 Carrera")
                    .year(2023)
                    .vin("1FA6P8CF0H5100006")
                    .category("Luxury")
                    .transmission("Automatic")
                    .seatingCapacity(2)
                    .fuelType("Gasoline")
                    .dailyRate(new BigDecimal("280.00"))
                    .locationCity("Los Angeles")
                    .latitude(34.0522)
                    .longitude(-118.2437)
                    .status(VehicleStatus.AVAILABLE)
                    .imageUrl("https://images.unsplash.com/photo-1614162692292-7ac56d7f7f1e?auto=format&fit=crop&w=800&q=80")
                    .build()
            );

            vehicleRepository.saveAll(initialVehicles);
            log.info("Successfully seeded {} sample vehicles.", initialVehicles.size());
        }
    }
}
