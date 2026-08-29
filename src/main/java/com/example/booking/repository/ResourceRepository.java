package com.example.booking.repository;

import com.example.booking.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>, JpaSpecificationExecutor<Resource> {

    List<Resource> findByIsAvailableTrue();

    Page<Resource> findByTypeIgnoreCase(String type, Pageable pageable);

    Page<Resource> findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(String name, String type, Pageable pageable);
}
