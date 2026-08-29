package com.example.booking.dto.response;

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
@Schema(description = "Resource details response")
public class ResourceResponse {

    @Schema(description = "Resource ID", example = "1")
    private Long id;

    @Schema(description = "Resource Name", example = "Conference Room A")
    private String name;

    @Schema(description = "Resource Type", example = "ROOM")
    private String type;

    @Schema(description = "Description", example = "Meeting room with 12 seats")
    private String description;

    @Schema(description = "Location", example = "Floor 2, Wing B")
    private String location;

    @Schema(description = "Capacity", example = "12")
    private Integer capacity;

    @Schema(description = "Base Price (decimal)", example = "50.00")
    private BigDecimal basePrice;

    @Schema(description = "Is Available", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Created Date", example = "2026-08-29T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last Updated Date", example = "2026-08-29T10:00:00")
    private LocalDateTime updatedAt;
}
