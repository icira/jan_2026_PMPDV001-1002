package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.RiskFactorConfiguration;
import com.policymanagementplatform.insurancecoreservice.domain.RiskFactorLevel;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.RiskFactorConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
LANES ADDED:
- RiskFactorAdminService for admin CRUD.
WHY:
- Part 2: admins manage risk adjustments by geography and building type.
*/
@Service
public class RiskFactorAdminService {

    private final RiskFactorConfigurationRepository riskRepository;

    public RiskFactorAdminService(RiskFactorConfigurationRepository riskRepository) {
        this.riskRepository = riskRepository;
    }

    @Transactional(readOnly = true)
    public List<RiskFactorConfiguration> list() {
        return riskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public RiskFactorConfiguration get(Long id) {
        return riskRepository.findById(id).orElseThrow(() -> new NotFoundException("Risk factor not found"));
    }

    @Transactional
    public RiskFactorConfiguration create(RiskFactorLevel level, Long referenceId, double adjustmentPercentage, boolean active) {
        return riskRepository.save(new RiskFactorConfiguration(level, referenceId, adjustmentPercentage, active));
    }

    @Transactional
    public RiskFactorConfiguration update(Long id, double adjustmentPercentage, boolean active) {
        RiskFactorConfiguration rf = get(id);
        rf.update(adjustmentPercentage, active);
        return rf;
    }
}
