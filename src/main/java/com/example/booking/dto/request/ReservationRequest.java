package com.example.booking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Reservation creation request (user identity is extracted from JWT token)")
public class ReservationRequest {

    @NotNull(message = "Resource ID is required")
    @Schema(description = "ID of the resource to reserve", example = "1")
    private Long resourceId;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be in the present or future")
    @Schema(description = "Reservation start time (ISO-8601)", example = "2026-09-01T10:00:00")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Schema(description = "Reservation end time (ISO-8601)", example = "2026-09-01T12:00:00")
    private LocalDateTime endTime;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total price must be zero or positive")
    @Schema(description = "Total reservation price (optional, auto-calculated from resource price if omitted)", example = "100.00")
    private BigDecimal totalPrice;

    @Schema(description = "Additional notes or requirements", example = "Client presentation with projector setup")
    private String notes;
}
