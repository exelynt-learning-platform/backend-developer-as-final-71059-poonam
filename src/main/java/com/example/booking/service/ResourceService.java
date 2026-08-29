package com.example.booking.service;

import com.example.booking.dto.request.ResourceRequest;
import com.example.booking.dto.response.PagedResponse;
import com.example.booking.dto.response.ResourceResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResourceService {

    ResourceResponse createResource(ResourceRequest request);

    ResourceResponse updateResource(Long id, ResourceRequest request);

    void deleteResource(Long id);

    ResourceResponse getResourceById(Long id);

    PagedResponse<ResourceResponse> getAllResources(Pageable pageable, String search, String type, Boolean isAvailable);

    List<ResourceResponse> getAvailableResources();
}
