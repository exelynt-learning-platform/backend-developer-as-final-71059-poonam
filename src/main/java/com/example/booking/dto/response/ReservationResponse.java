package com.example.booking.dto.response;

import com.example.booking.entity.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Reservation details response")
public class ReservationResponse {

    @Schema(description = "Reservation ID", example = "1")
    private Long id;

    @Schema(description = "Booked Resource ID", example = "2")
    private Long resourceId;

    @Schema(description = "Resource Name", example = "Conference Room A")
    private String resourceName;

    @Schema(description = "Resource Type", example = "ROOM")
    private String resourceType;

    @Schema(description = "User ID who booked", example = "3")
    private Long userId;

    @Schema(description = "Username who booked", example = "john_doe")
    private String username;

    @Schema(description = "User Email", example = "john@example.com")
    private String userEmail;

    @Schema(description = "Reservation start time", example = "2026-09-01T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "Reservation end time", example = "2026-09-01T12:00:00")
    private LocalDateTime endTime;

    @Schema(description = "Reservation Status (PENDING, CONFIRMED, CANCELLED)", example = "CONFIRMED")
    private ReservationStatus status;

    @Schema(description = "Total reservation price (decimal)", example = "100.00")
    private BigDecimal totalPrice;

    @Schema(description = "Reservation Notes", example = "Project presentation")
    private String notes;

    @Schema(description = "Created Timestamp", example = "2026-08-29T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last Updated Timestamp", example = "2026-08-29T10:00:00")
    private LocalDateTime updatedAt;
}
