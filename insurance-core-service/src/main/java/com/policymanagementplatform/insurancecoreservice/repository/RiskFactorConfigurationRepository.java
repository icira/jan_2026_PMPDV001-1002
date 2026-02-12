package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.RiskFactorConfiguration;
import com.policymanagementplatform.insurancecoreservice.domain.RiskFactorLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
LANES ADDED:
- RiskFactorConfigurationRepository with active lookup.
WHY:
- Part 2: policy premium calculation must include only active risk factors.
*/
public interface RiskFactorConfigurationRepository extends JpaRepository<RiskFactorConfiguration, Long> {
    List<RiskFactorConfiguration> findByLevelAndReferenceIdAndActiveTrue(RiskFactorLevel level, Long referenceId);
}
