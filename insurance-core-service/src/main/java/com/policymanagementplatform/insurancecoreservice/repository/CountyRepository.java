package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.County;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountyRepository extends JpaRepository<County, Long> {
    Page<County> findByCountry_Id(Long countryId, Pageable pageable);

    Optional<County> findByCountry_IdAndNameIgnoreCase(Long countryId, String name);

    boolean existsByCountry_IdAndNameIgnoreCase(Long countryId, String name);
}
