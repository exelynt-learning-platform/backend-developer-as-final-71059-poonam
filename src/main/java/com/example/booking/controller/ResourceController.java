package com.example.booking.controller;

import com.example.booking.dto.request.ResourceRequest;
import com.example.booking.dto.response.ApiResponse;
import com.example.booking.dto.response.PagedResponse;
import com.example.booking.dto.response.ResourceResponse;
import com.example.booking.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Resources", description = "Endpoints for viewing and managing bookable resources")
@SecurityRequirement(name = "Bearer Authentication")
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    @Operation(summary = "Get list of resources", description = "Accessible by USER and ADMIN. Supports search, type filter, availability, pagination, and sorting.")
    public ResponseEntity<ApiResponse<PagedResponse<ResourceResponse>>> getAllResources(
            @Parameter(description = "Search keyword in name, description, or location")
            @RequestParam(required = false) String search,
            @Parameter(description = "Filter by resource type (e.g. ROOM, VEHICLE, EQUIPMENT)")
            @RequestParam(required = false) String type,
            @Parameter(description = "Filter by availability status")
            @RequestParam(required = false) Boolean isAvailable,
            @Parameter(description = "Page index (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<ResourceResponse> response = resourceService.getAllResources(pageable, search, type, isAvailable);
        return ResponseEntity.ok(ApiResponse.ok("Resources retrieved successfully", response));
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available resources", description = "Accessible by USER and ADMIN. Returns list of available resources.")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getAvailableResources() {
        List<ResourceResponse> resources = resourceService.getAvailableResources();
        return ResponseEntity.ok(ApiResponse.ok("Available resources retrieved", resources));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resource by ID", description = "Accessible by USER and ADMIN.")
    public ResponseEntity<ApiResponse<ResourceResponse>> getResourceById(@PathVariable Long id) {
        ResourceResponse resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(ApiResponse.ok("Resource details retrieved", resource));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new resource", description = "ADMIN role only. Creates a new bookable resource.")
    public ResponseEntity<ApiResponse<ResourceResponse>> createResource(@Valid @RequestBody ResourceRequest request) {
        ResourceResponse created = resourceService.createResource(request);
        return new ResponseEntity<>(ApiResponse.ok("Resource created successfully", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing resource", description = "ADMIN role only. Updates resource details.")
    public ResponseEntity<ApiResponse<ResourceResponse>> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request
    ) {
        ResourceResponse updated = resourceService.updateResource(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Resource updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a resource", description = "ADMIN role only. Deletes a resource.")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.message("Resource deleted successfully"));
    }
}
