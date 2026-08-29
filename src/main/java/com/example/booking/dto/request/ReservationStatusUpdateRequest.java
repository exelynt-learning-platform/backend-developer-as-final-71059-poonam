package com.example.booking.dto.request;

import com.example.booking.entity.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Reservation status update request")
public class ReservationStatusUpdateRequest {

    @NotNull(message = "Status is required")
    @Schema(description = "New reservation status (PENDING, CONFIRMED, CANCELLED)", example = "CONFIRMED")
    private ReservationStatus status;
}
