package com.example.booking.service;

import com.example.booking.dto.request.ReservationRequest;
import com.example.booking.dto.request.ReservationStatusUpdateRequest;
import com.example.booking.dto.response.PagedResponse;
import com.example.booking.dto.response.ReservationResponse;
import com.example.booking.entity.ReservationStatus;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request, Long currentUserId);

    ReservationResponse getReservationById(Long id, Long currentUserId, boolean isAdmin);

    PagedResponse<ReservationResponse> getAllReservations(
            Long currentUserId,
            boolean isAdmin,
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Long resourceId,
            Long filterUserId,
            LocalDateTime startAfter,
            LocalDateTime endBefore,
            Pageable pageable
    );

    ReservationResponse updateReservationStatus(Long id, ReservationStatusUpdateRequest request, Long currentUserId, boolean isAdmin);

    void deleteReservation(Long id, Long currentUserId, boolean isAdmin);
}
