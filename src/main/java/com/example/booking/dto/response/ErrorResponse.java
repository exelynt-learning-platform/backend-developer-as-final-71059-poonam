package com.example.booking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard Error Response")
public class ErrorResponse {

    @Schema(description = "HTTP Status Code", example = "400")
    private int status;

    @Schema(description = "HTTP Error Name", example = "Bad Request")
    private String error;

    @Schema(description = "Error message", example = "Resource not found with ID: 123")
    private String message;

    @Schema(description = "Requested URI path", example = "/api/reservations")
    private String path;

    @Schema(description = "Detailed field validation errors")
    private Map<String, String> validationErrors;

    @Schema(description = "Error timestamp", example = "2026-08-29T09:30:00")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
