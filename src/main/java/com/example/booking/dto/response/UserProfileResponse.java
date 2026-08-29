package com.example.booking.dto.response;

import com.example.booking.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User profile response")
public class UserProfileResponse {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "Username", example = "admin")
    private String username;

    @Schema(description = "Email", example = "admin@example.com")
    private String email;

    @Schema(description = "Full Name", example = "System Administrator")
    private String fullName;

    @Schema(description = "Role", example = "ROLE_ADMIN")
    private Role role;

    @Schema(description = "Account created timestamp", example = "2026-08-29T10:00:00")
    private LocalDateTime createdAt;
}
