package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.repository.PolicyRepository;
import com.policymanagementplatform.insurancecoreservice.web.dto.PolicyReportRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/*
LANES ADDED:
- Admin report service for policy aggregates.
WHY:
- Part 2 requires admin reporting by geography, broker and period.
*/
@Service
public class AdminReportService {

    private final PolicyRepository policyRepository;

    public AdminReportService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Transactional(readOnly = true)
    public Page<PolicyReportRow> policyReport(LocalDate startDate,
                                              LocalDate endDate,
                                              Long brokerId,
                                              Long countryId,
                                              Long countyId,
                                              Long cityId,
                                              Pageable pageable) {
        return policyRepository.reportByGeographyAndBroker(
                        startDate,
                        endDate,
                        brokerId,
                        countryId,
                        countyId,
                        cityId,
                        pageable)
                .map(p -> new PolicyReportRow(
                        p.getCountryId(),
                        p.getCountryName(),
                        p.getCountyId(),
                        p.getCountyName(),
                        p.getCityId(),
                        p.getCityName(),
                        p.getBrokerId(),
                        p.getBrokerCode(),
                        p.getBrokerName(),
                        p.getPolicyCount(),
                        p.getTotalFinalPremium()
                ));
    }
}