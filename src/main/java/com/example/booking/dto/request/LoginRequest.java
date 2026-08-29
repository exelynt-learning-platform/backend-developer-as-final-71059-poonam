package com.example.booking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Login credentials request")
public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    @Schema(description = "Username or Email address", example = "admin")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "Password", example = "admin123")
    private String password;
}
