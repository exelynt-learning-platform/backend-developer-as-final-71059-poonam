package com.example.booking.util;

import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Resource;
import com.example.booking.entity.Role;
import com.example.booking.entity.User;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedResources();
        seedReservations();
    }

    private void seedUsers() {
        log.info("Ensuring users are initialized for Poonam Memane's Resource Booking System...");

        // Admin User - Poonam Memane
        userRepository.findByUsername("poonam").ifPresentOrElse(
                existing -> {
                    existing.setPassword(passwordEncoder.encode("poonam123"));
                    existing.setFullName("Poonam Memane");
                    existing.setRole(Role.ROLE_ADMIN);
                    existing.setEmail("poonam.memane@example.com");
                    userRepository.save(existing);
                },
                () -> {
                    User admin = User.builder()
                            .username("poonam")
                            .password(passwordEncoder.encode("poonam123"))
                            .email("poonam.memane@example.com")
                            .fullName("Poonam Memane")
                            .role(Role.ROLE_ADMIN)
                            .build();
                    userRepository.save(admin);
                }
        );

        // Legacy Admin User alias
        userRepository.findByUsername("admin").ifPresentOrElse(
                existing -> {
                    existing.setPassword(passwordEncoder.encode("admin123"));
                    existing.setFullName("Poonam Memane (Admin)");
                    existing.setRole(Role.ROLE_ADMIN);
                    userRepository.save(existing);
                },
                () -> {
                    User adminAlias = User.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("admin123"))
                            .email("admin@example.com")
                            .fullName("Poonam Memane (Admin)")
                            .role(Role.ROLE_ADMIN)
                            .build();
                    userRepository.save(adminAlias);
                }
        );

        // Standard User
        userRepository.findByUsername("user").ifPresentOrElse(
                existing -> {
                    existing.setPassword(passwordEncoder.encode("user123"));
                    existing.setFullName("Poonam Memane");
                    existing.setRole(Role.ROLE_USER);
                    userRepository.save(existing);
                },
                () -> {
                    User user1 = User.builder()
                            .username("user")
                            .password(passwordEncoder.encode("user123"))
                            .email("poonam.user@example.com")
                            .fullName("Poonam Memane")
                            .role(Role.ROLE_USER)
                            .build();
                    userRepository.save(user1);
                }
        );

        log.info("Users ready: poonam/poonam123 [ADMIN], admin/admin123 [ADMIN], user/user123 [USER].");
    }

    private void seedResources() {
        if (resourceRepository.count() == 0) {
            log.info("Seeding initial resources with INR pricing...");

            Resource room1 = Resource.builder()
                    .name("Executive Conference Room Apollo")
                    .type("ROOM")
                    .description("Executive boardroom with 4K UHD conferencing, digital smart board, and 16 executive ergonomic leather chairs.")
                    .location("Tower A, 4th Floor, Tech Park")
                    .capacity(16)
                    .basePrice(new BigDecimal("2500.00"))
                    .isAvailable(true)
                    .build();
            resourceRepository.save(room1);

            Resource room2 = Resource.builder()
                    .name("Innovation Design Studio")
                    .type("ROOM")
                    .description("Collaborative design workshop space with dual smart displays, acoustic baffling, and modular team tables.")
                    .location("Innovation Hub, Floor 2")
                    .capacity(25)
                    .basePrice(new BigDecimal("4000.00"))
                    .isAvailable(true)
                    .build();
            resourceRepository.save(room2);

            Resource equipment1 = Resource.builder()
                    .name("Sony 4K Pro Video Production Kit")
                    .type("EQUIPMENT")
                    .description("Full 4K cinema camera package with wireless lavalier mics, teleprompter, and studio LED lighting panels.")
                    .location("Media Center, Tech Bay 3")
                    .capacity(1)
                    .basePrice(new BigDecimal("1200.00"))
                    .isAvailable(true)
                    .build();
            resourceRepository.save(equipment1);

            Resource vehicle1 = Resource.builder()
                    .name("Executive VIP Transport Shuttle")
                    .type("VEHICLE")
                    .description("Premium 8-passenger all-electric vehicle with high-speed Wi-Fi, executive workspace seating, and GPS tracking.")
                    .location("VIP Fleet Bay 1")
                    .capacity(8)
                    .basePrice(new BigDecimal("3500.00"))
                    .isAvailable(true)
                    .build();
            resourceRepository.save(vehicle1);

            log.info("Seed resources created with INR prices (₹2500/hr, ₹4000/hr, ₹1200/hr, ₹3500/hr).");
        }
    }

    private void seedReservations() {
        if (reservationRepository.count() == 0) {
            log.info("Seeding initial sample reservations for Poonam Memane...");

            userRepository.findByUsername("poonam").ifPresent(poonam -> {
                resourceRepository.findAll().stream().findFirst().ifPresent(resource -> {
                    Reservation reservation1 = Reservation.builder()
                            .user(poonam)
                            .resource(resource)
                            .startTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0))
                            .endTime(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0))
                            .status(ReservationStatus.CONFIRMED)
                            .totalPrice(new BigDecimal("5000.00"))
                            .notes("Poonam Memane - Quarterly Technical Strategy & Architecture Review")
                            .build();
                    reservationRepository.save(reservation1);
                });
            });

            userRepository.findByUsername("user").ifPresent(user -> {
                resourceRepository.findAll().stream().skip(1).findFirst().ifPresent(resource -> {
                    Reservation reservation2 = Reservation.builder()
                            .user(user)
                            .resource(resource)
                            .startTime(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0).withSecond(0))
                            .endTime(LocalDateTime.now().plusDays(2).withHour(16).withMinute(0).withSecond(0))
                            .status(ReservationStatus.PENDING)
                            .totalPrice(new BigDecimal("8000.00"))
                            .notes("Poonam Memane - Sprint Planning & Resource Allocation")
                            .build();
                    reservationRepository.save(reservation2);
                });
            });

            log.info("Seed reservations created successfully.");
        }
    }
}
