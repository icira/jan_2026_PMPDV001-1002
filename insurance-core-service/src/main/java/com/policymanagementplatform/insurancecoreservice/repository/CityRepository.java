package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Page<City> findByCounty_Id(Long countyId, Pageable pageable);

    Optional<City> findByCounty_IdAndCodeIgnoreCase(Long countyId, String code);
}
