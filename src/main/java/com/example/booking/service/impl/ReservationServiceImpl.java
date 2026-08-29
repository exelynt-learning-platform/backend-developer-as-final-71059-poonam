package com.example.booking.service.impl;

import com.example.booking.dto.request.ReservationRequest;
import com.example.booking.dto.request.ReservationStatusUpdateRequest;
import com.example.booking.dto.response.PagedResponse;
import com.example.booking.dto.response.ReservationResponse;
import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Resource;
import com.example.booking.entity.User;
import com.example.booking.exception.AccessDeniedCustomException;
import com.example.booking.exception.BadRequestException;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.repository.ReservationSpecification;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.repository.UserRepository;
import com.example.booking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, Long currentUserId) {
        // Enforce user identity from JWT
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", request.getResourceId()));

        if (Boolean.FALSE.equals(resource.getIsAvailable())) {
            throw new BadRequestException("Resource '" + resource.getName() + "' is currently marked as unavailable for booking.");
        }

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BadRequestException("Start time and End time must not be null.");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BadRequestException("Reservation start time must be before end time.");
        }

        // Check for conflicting reservations for this resource
        List<Reservation> conflicts = reservationRepository.findOverlappingReservations(
                resource.getId(),
                request.getStartTime(),
                request.getEndTime(),
                ReservationStatus.CANCELLED
        );

        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Resource is already reserved during the requested time slot.");
        }

        // Calculate price if not provided, or store provided price as decimal
        BigDecimal finalPrice = request.getTotalPrice();
        if (finalPrice == null) {
            long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
            double hours = Math.max(1.0, (double) minutes / 60.0);
            BigDecimal base = resource.getBasePrice() != null ? resource.getBasePrice() : BigDecimal.ZERO;
            finalPrice = base.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
        } else {
            finalPrice = finalPrice.setScale(2, RoundingMode.HALF_UP);
        }

        Reservation reservation = Reservation.builder()
                .resource(resource)
                .user(user)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(ReservationStatus.PENDING)
                .totalPrice(finalPrice)
                .notes(request.getNotes())
                .build();

        Reservation saved = reservationRepository.save(reservation);
        log.info("Created reservation id: {} for user: {} on resource: {}", saved.getId(), user.getUsername(), resource.getName());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id, Long currentUserId, boolean isAdmin) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        // Authorization check: non-admin can only view their own reservation
        if (!isAdmin && !reservation.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedCustomException("Access denied: You are only allowed to view your own reservations.");
        }

        return mapToResponse(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReservationResponse> getAllReservations(
            Long currentUserId,
            boolean isAdmin,
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Long resourceId,
            Long filterUserId,
            LocalDateTime startAfter,
            LocalDateTime endBefore,
            Pageable pageable) {

        // Strict RBAC filtering: Regular user can ONLY query their own reservations
        Long effectiveUserId = isAdmin ? filterUserId : currentUserId;

        Specification<Reservation> spec = ReservationSpecification.filterReservations(
                effectiveUserId,
                status,
                minPrice,
                maxPrice,
                resourceId,
                startAfter,
                endBefore
        );

        Page<Reservation> pageResult = reservationRepository.findAll(spec, pageable);
        Page<ReservationResponse> responsePage = pageResult.map(this::mapToResponse);

        return PagedResponse.from(responsePage);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservationStatus(
            Long id,
            ReservationStatusUpdateRequest request,
            Long currentUserId,
            boolean isAdmin) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        if (!isAdmin) {
            // User can only cancel their own reservation
            if (!reservation.getUser().getId().equals(currentUserId)) {
                throw new AccessDeniedCustomException("Access denied: You cannot modify reservations belonging to other users.");
            }

            if (request.getStatus() != ReservationStatus.CANCELLED) {
                throw new BadRequestException("Regular users are only permitted to cancel their own reservations.");
            }
        }

        reservation.setStatus(request.getStatus());
        Reservation updated = reservationRepository.save(reservation);
        log.info("Updated reservation id: {} status to: {}", id, request.getStatus());

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteReservation(Long id, Long currentUserId, boolean isAdmin) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        if (!isAdmin) {
            if (!reservation.getUser().getId().equals(currentUserId)) {
                throw new AccessDeniedCustomException("Access denied: You cannot delete reservations belonging to other users.");
            }
            // Cancel user's reservation
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
            log.info("User {} cancelled reservation id: {}", currentUserId, id);
        } else {
            reservationRepository.delete(reservation);
            log.info("Admin deleted reservation id: {}", id);
        }
    }

    public ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .resourceId(reservation.getResource().getId())
                .resourceName(reservation.getResource().getName())
                .resourceType(reservation.getResource().getType())
                .userId(reservation.getUser().getId())
                .username(reservation.getUser().getUsername())
                .userEmail(reservation.getUser().getEmail())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .totalPrice(reservation.getTotalPrice())
                .notes(reservation.getNotes())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
}
