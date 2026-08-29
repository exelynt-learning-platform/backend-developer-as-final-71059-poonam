package com.example.booking.service.impl;

import com.example.booking.dto.request.ResourceRequest;
import com.example.booking.dto.response.PagedResponse;
import com.example.booking.dto.response.ResourceResponse;
import com.example.booking.entity.Resource;
import com.example.booking.exception.ResourceNotFoundException;
import com.example.booking.repository.ResourceRepository;
import com.example.booking.service.ResourceService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;

    @Override
    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {
        Resource resource = Resource.builder()
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .basePrice(request.getBasePrice())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .build();

        Resource saved = resourceRepository.save(resource);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));

        resource.setName(request.getName());
        resource.setType(request.getType());
        resource.setDescription(request.getDescription());
        resource.setLocation(request.getLocation());
        resource.setCapacity(request.getCapacity());
        resource.setBasePrice(request.getBasePrice());
        if (request.getIsAvailable() != null) {
            resource.setIsAvailable(request.getIsAvailable());
        }

        Resource updated = resourceRepository.save(resource);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteResource(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));

        resourceRepository.delete(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", id));

        return mapToResponse(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ResourceResponse> getAllResources(Pageable pageable, String search, String type, Boolean isAvailable) {
        Specification<Resource> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(search)) {
                String pattern = "%" + search.toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                Predicate locMatch = cb.like(cb.lower(root.get("location")), pattern);
                predicates.add(cb.or(nameMatch, descMatch, locMatch));
            }

            if (StringUtils.hasText(type)) {
                predicates.add(cb.equal(cb.lower(root.get("type")), type.toLowerCase()));
            }

            if (isAvailable != null) {
                predicates.add(cb.equal(root.get("isAvailable"), isAvailable));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Resource> resourcePage = resourceRepository.findAll(spec, pageable);
        Page<ResourceResponse> responsePage = resourcePage.map(this::mapToResponse);

        return PagedResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getAvailableResources() {
        return resourceRepository.findByIsAvailableTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ResourceResponse mapToResponse(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .type(resource.getType())
                .description(resource.getDescription())
                .location(resource.getLocation())
                .capacity(resource.getCapacity())
                .basePrice(resource.getBasePrice())
                .isAvailable(resource.getIsAvailable())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}
