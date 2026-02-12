package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.FeeConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/*
LANES ADDED:
- findEffectiveFees(date) query.
WHY:
- Part 2 non-functional: avoid loading too much data; only load active/effective fees for a given date.
*/
public interface FeeConfigurationRepository extends JpaRepository<FeeConfiguration, Long> {

    @Query("""
        select f from FeeConfiguration f
        where f.active = true
          and f.effectiveFrom <= :date
          and (f.effectiveTo is null or f.effectiveTo >= :date)
        """)
    List<FeeConfiguration> findEffectiveFees(LocalDate date);
}
