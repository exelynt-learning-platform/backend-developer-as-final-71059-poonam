package com.example.booking.controller;

import com.example.booking.dto.request.ReservationRequest;
import com.example.booking.dto.request.ReservationStatusUpdateRequest;
import com.example.booking.dto.response.ApiResponse;
import com.example.booking.dto.response.PagedResponse;
import com.example.booking.dto.response.ReservationResponse;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Role;
import com.example.booking.security.UserDetailsImpl;
import com.example.booking.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping({"/api/reservations", "/reservations"})
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Endpoints for creating, filtering, and managing resource reservations")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(
            summary = "Create a reservation",
            description = "Creates a new reservation. USER identity is automatically extracted from JWT, NOT from request body."
    )
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(
            @Valid @RequestBody ReservationRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        ReservationResponse response = reservationService.createReservation(request, currentUser.getId());
        return new ResponseEntity<>(ApiResponse.ok("Reservation created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Get reservations with filtering and pagination",
            description = "ADMIN can view all reservations or filter by any user. USER can only view their own reservations. Supports filtering by status, minPrice, maxPrice, pagination, and sorting."
    )
    public ResponseEntity<ApiResponse<PagedResponse<ReservationResponse>>> getAllReservations(
            @Parameter(description = "Filter by status (PENDING, CONFIRMED, CANCELLED)")
            @RequestParam(required = false) ReservationStatus status,
            @Parameter(description = "Filter by minimum price (decimal)")
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Filter by maximum price (decimal)")
            @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Filter by resource ID")
            @RequestParam(required = false) Long resourceId,
            @Parameter(description = "Filter by user ID (ADMIN only)")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "Filter reservations starting after (ISO DateTime)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAfter,
            @Parameter(description = "Filter reservations ending before (ISO DateTime)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endBefore,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by property (e.g. createdAt, totalPrice, startTime)")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc / desc)")
            @RequestParam(defaultValue = "desc") String sortDirection,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<ReservationResponse> response = reservationService.getAllReservations(
                currentUser.getId(),
                isAdmin,
                status,
                minPrice,
                maxPrice,
                resourceId,
                userId,
                startAfter,
                endBefore,
                pageable
        );

        return ResponseEntity.ok(ApiResponse.ok("Reservations retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get reservation by ID",
            description = "ADMIN can view any reservation. USER can view only their own reservation."
    )
    public ResponseEntity<ApiResponse<ReservationResponse>> getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        ReservationResponse response = reservationService.getReservationById(id, currentUser.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Reservation details retrieved", response));
    }

    @PutMapping("/{id}/status")
    @Operation(
            summary = "Update reservation status",
            description = "ADMIN can change status to PENDING, CONFIRMED, or CANCELLED. USER can only cancel (CANCELLED) their own reservation."
    )
    public ResponseEntity<ApiResponse<ReservationResponse>> updateReservationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReservationStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        ReservationResponse response = reservationService.updateReservationStatus(id, request, currentUser.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.ok("Reservation status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Cancel or delete a reservation",
            description = "ADMIN deletes reservation from database. USER cancels their own reservation."
    )
    public ResponseEntity<ApiResponse<Void>> deleteReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        reservationService.deleteReservation(id, currentUser.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.message(isAdmin ? "Reservation deleted successfully" : "Reservation cancelled successfully"));
    }
}
