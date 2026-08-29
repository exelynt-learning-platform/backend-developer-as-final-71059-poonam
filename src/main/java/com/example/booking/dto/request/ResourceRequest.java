package com.example.booking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resource creation or update request")
public class ResourceRequest {

    @NotBlank(message = "Resource name is required")
    @Schema(description = "Name of the resource", example = "Conference Room A")
    private String name;

    @NotBlank(message = "Resource type is required")
    @Schema(description = "Type/Category of resource (e.g. ROOM, VEHICLE, EQUIPMENT)", example = "ROOM")
    private String type;

    @Schema(description = "Description of the resource", example = "Executive meeting room with projector and 12 seats")
    private String description;

    @Schema(description = "Physical location or department", example = "Building 1, 3rd Floor")
    private String location;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Schema(description = "Max capacity or quantity", example = "12")
    private Integer capacity;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base price cannot be negative")
    @Schema(description = "Base price per hour or slot", example = "50.00")
    private BigDecimal basePrice;

    @Schema(description = "Availability flag", example = "true")
    @Builder.Default
    private Boolean isAvailable = true;
}
