package com.example.booking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Authentication login response containing JWT token")
public class AuthResponse {

    @Schema(description = "JWT Bearer Access Token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String type = "Bearer";

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Username", example = "admin")
    private String username;

    @Schema(description = "Email", example = "admin@example.com")
    private String email;

    @Schema(description = "Assigned Role", example = "ROLE_ADMIN")
    private String role;

    @Schema(description = "Full Name", example = "System Administrator")
    private String fullName;

    @Schema(description = "Token validity in milliseconds", example = "86400000")
    private Long expiresIn;
}
