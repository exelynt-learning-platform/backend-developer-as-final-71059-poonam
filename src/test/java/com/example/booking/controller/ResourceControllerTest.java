package com.example.booking.controller;

import com.example.booking.dto.request.LoginRequest;
import com.example.booking.dto.request.ResourceRequest;
import com.example.booking.dto.response.ApiResponse;
import com.example.booking.dto.response.AuthResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // Obtain Admin Token
        LoginRequest adminLogin = LoginRequest.builder()
                .username("admin")
                .password("admin123")
                .build();

        MvcResult adminResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<AuthResponse> adminAuth = objectMapper.readValue(
                adminResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<AuthResponse>>() {}
        );
        adminToken = "Bearer " + adminAuth.getData().getToken();

        // Obtain User Token
        LoginRequest userLogin = LoginRequest.builder()
                .username("user")
                .password("user123")
                .build();

        MvcResult userResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<AuthResponse> userAuth = objectMapper.readValue(
                userResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<AuthResponse>>() {}
        );
        userToken = "Bearer " + userAuth.getData().getToken();
    }

    @Test
    @DisplayName("Both USER and ADMIN should be able to view resources")
    void testGetResourcesAllowedForUserAndAdmin() throws Exception {
        // USER request
        mockMvc.perform(get("/api/resources")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        // ADMIN request
        mockMvc.perform(get("/api/resources")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("USER should be forbidden (403) from creating resources")
    void testUserCannotCreateResource() throws Exception {
        ResourceRequest request = ResourceRequest.builder()
                .name("Unauthorized Meeting Room")
                .type("ROOM")
                .basePrice(new BigDecimal("50.00"))
                .build();

        mockMvc.perform(post("/api/resources")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN should have full CRUD access to create, update, and delete resources")
    void testAdminResourceCrud() throws Exception {
        // 1. Create Resource
        ResourceRequest createRequest = ResourceRequest.builder()
                .name("Podcast Studio B")
                .type("STUDIO")
                .description("Soundproof podcasting studio")
                .location("Floor 4, Media Wing")
                .capacity(4)
                .basePrice(new BigDecimal("35.00"))
                .isAvailable(true)
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/resources")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Podcast Studio B"))
                .andExpect(jsonPath("$.data.basePrice").value(35.00))
                .andReturn();

        // Extract ID
        ApiResponse<com.example.booking.dto.response.ResourceResponse> created = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<com.example.booking.dto.response.ResourceResponse>>() {}
        );
        Long resourceId = created.getData().getId();

        // 2. Update Resource
        ResourceRequest updateRequest = ResourceRequest.builder()
                .name("Podcast Studio B - Upgraded")
                .type("STUDIO")
                .description("Updated description")
                .capacity(6)
                .basePrice(new BigDecimal("40.00"))
                .isAvailable(true)
                .build();

        mockMvc.perform(put("/api/resources/" + resourceId)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Podcast Studio B - Upgraded"))
                .andExpect(jsonPath("$.data.basePrice").value(40.00));

        // 3. Delete Resource
        mockMvc.perform(delete("/api/resources/" + resourceId)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Resource deleted successfully"));
    }
}
