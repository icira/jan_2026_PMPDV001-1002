package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.FeeConfiguration;
import com.policymanagementplatform.insurancecoreservice.domain.FeeConfigurationType;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.FeeConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/*
LANES ADDED:
- FeeConfigurationAdminService for admin CRUD.
WHY:
- Part 2: admins manage fee configurations including validity periods and activation.
*/
@Service
public class FeeConfigurationAdminService {

    private final FeeConfigurationRepository feeRepository;

    public FeeConfigurationAdminService(FeeConfigurationRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    @Transactional(readOnly = true)
    public List<FeeConfiguration> list() {
        return feeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public FeeConfiguration get(Long id) {
        return feeRepository.findById(id).orElseThrow(() -> new NotFoundException("Fee configuration not found"));
    }

    @Transactional
    public FeeConfiguration create(String name, FeeConfigurationType type, double percentage, LocalDate effectiveFrom, LocalDate effectiveTo, boolean active) {
        return feeRepository.save(new FeeConfiguration(name, type, percentage, effectiveFrom, effectiveTo, active));
    }

    @Transactional
    public FeeConfiguration update(Long id, double percentage, LocalDate effectiveFrom, LocalDate effectiveTo, boolean active) {
        FeeConfiguration fee = get(id);
        fee.update(percentage, effectiveFrom, effectiveTo, active);
        return fee;
    }
}
