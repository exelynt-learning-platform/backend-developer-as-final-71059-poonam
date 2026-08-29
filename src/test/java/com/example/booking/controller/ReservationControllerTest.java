package com.example.booking.controller;

import com.example.booking.dto.request.LoginRequest;
import com.example.booking.dto.request.ReservationRequest;
import com.example.booking.dto.request.ReservationStatusUpdateRequest;
import com.example.booking.dto.response.ApiResponse;
import com.example.booking.dto.response.AuthResponse;
import com.example.booking.dto.response.ReservationResponse;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.entity.Resource;
import com.example.booking.repository.ResourceRepository;
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
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceRepository resourceRepository;

    private String adminToken;
    private String userToken;
    private String johnToken;
    private Long sampleResourceId;

    @BeforeEach
    void setUp() throws Exception {
        // Admin Token
        MvcResult adminResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoginRequest.builder().username("admin").password("admin123").build())))
                .andExpect(status().isOk())
                .andReturn();
        adminToken = "Bearer " + objectMapper.readValue(adminResult.getResponse().getContentAsString(), new TypeReference<ApiResponse<AuthResponse>>() {}).getData().getToken();

        // User Token (username: user)
        MvcResult userResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoginRequest.builder().username("user").password("user123").build())))
                .andExpect(status().isOk())
                .andReturn();
        userToken = "Bearer " + objectMapper.readValue(userResult.getResponse().getContentAsString(), new TypeReference<ApiResponse<AuthResponse>>() {}).getData().getToken();

        // Poonam Token (username: poonam)
        MvcResult poonamResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoginRequest.builder().username("poonam").password("poonam123").build())))
                .andExpect(status().isOk())
                .andReturn();
        johnToken = "Bearer " + objectMapper.readValue(poonamResult.getResponse().getContentAsString(), new TypeReference<ApiResponse<AuthResponse>>() {}).getData().getToken();

        Resource resource = resourceRepository.findAll().stream().findFirst().orElseThrow();
        sampleResourceId = resource.getId();
    }

    @Test
    @DisplayName("USER creates reservation where user identity is taken from JWT")
    void testCreateReservationTakesUserIdentityFromJwt() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(5).withHour(9).withMinute(0).withSecond(0);
        LocalDateTime end = LocalDateTime.now().plusDays(5).withHour(11).withMinute(0).withSecond(0);

        ReservationRequest request = ReservationRequest.builder()
                .resourceId(sampleResourceId)
                .startTime(start)
                .endTime(end)
                .totalPrice(new BigDecimal("120.50"))
                .notes("Executive Client Demo")
                .build();

        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("user"))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.totalPrice").value(120.50));
    }

    @Test
    @DisplayName("USER should only view their own reservations; ADMIN can view all")
    void testReservationVisibilityByRole() throws Exception {
        // USER query (should only have reservations where username is 'user')
        mockMvc.perform(get("/api/reservations")
                        .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[?(@.username != 'user')]").doesNotExist());

        // ADMIN query (should have multiple reservations across different users)
        mockMvc.perform(get("/api/reservations")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").isNotEmpty());
    }

    @Test
    @DisplayName("Filter reservations by status, minPrice, maxPrice, pagination, and sorting")
    void testFilterAndPagination() throws Exception {
        mockMvc.perform(get("/api/reservations")
                        .header("Authorization", adminToken)
                        .param("status", "CONFIRMED")
                        .param("minPrice", "50.00")
                        .param("maxPrice", "10000.00")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "totalPrice")
                        .param("sortDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(5));
    }

    @Test
    @DisplayName("USER can cancel their own reservation, ADMIN can confirm status")
    void testStatusLifecycle() throws Exception {
        // 1. Create a reservation with john_doe
        LocalDateTime start = LocalDateTime.now().plusDays(10).withHour(13).withMinute(0).withSecond(0);
        LocalDateTime end = LocalDateTime.now().plusDays(10).withHour(15).withMinute(0).withSecond(0);

        ReservationRequest request = ReservationRequest.builder()
                .resourceId(sampleResourceId)
                .startTime(start)
                .endTime(end)
                .totalPrice(new BigDecimal("150.00"))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/reservations")
                        .header("Authorization", johnToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        ApiResponse<ReservationResponse> res = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<ReservationResponse>>() {}
        );
        Long reservationId = res.getData().getId();

        // 2. ADMIN confirms the reservation
        ReservationStatusUpdateRequest adminUpdate = ReservationStatusUpdateRequest.builder()
                .status(ReservationStatus.CONFIRMED)
                .build();

        mockMvc.perform(put("/api/reservations/" + reservationId + "/status")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));

        // 3. USER cancels their own reservation
        ReservationStatusUpdateRequest userCancel = ReservationStatusUpdateRequest.builder()
                .status(ReservationStatus.CANCELLED)
                .build();

        mockMvc.perform(put("/api/reservations/" + reservationId + "/status")
                        .header("Authorization", johnToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCancel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }
}
