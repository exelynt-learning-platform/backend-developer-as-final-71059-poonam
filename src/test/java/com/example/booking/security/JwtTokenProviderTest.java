package com.example.booking.security;

import com.example.booking.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "ResourceBookingSystemSecretKey2026VerySecureKey123456789");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 86400000L);
    }

    @Test
    @DisplayName("Should generate, validate, and extract claims from JWT token")
    void testGenerateAndValidateToken() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .password("encoded_pass")
                .role(Role.ROLE_USER)
                .authorities(Collections.emptyList())
                .build();

        String token = jwtTokenProvider.generateTokenFromUserDetails(userDetails);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromJWT(token));
        assertEquals(1L, jwtTokenProvider.getUserIdFromJWT(token));
    }

    @Test
    @DisplayName("Should return false for invalid JWT token")
    void testInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }
}
