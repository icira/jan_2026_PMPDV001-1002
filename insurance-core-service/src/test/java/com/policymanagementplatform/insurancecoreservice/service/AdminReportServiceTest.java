package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.repository.PolicyReportProjection;
import com.policymanagementplatform.insurancecoreservice.repository.PolicyRepository;
import com.policymanagementplatform.insurancecoreservice.web.dto.PolicyReportRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminReportServiceTest {

    private PolicyRepository policyRepository;
    private AdminReportService service;

    @BeforeEach
    void setUp() {
        policyRepository = mock(PolicyRepository.class);
        service = new AdminReportService(policyRepository);
    }

    @Test
    void policyReportShouldMapProjectionToDto() {
        PolicyReportProjection projection = mock(PolicyReportProjection.class);
        when(projection.getCountryId()).thenReturn(1L);
        when(projection.getCountryName()).thenReturn("Romania");
        when(projection.getCountyId()).thenReturn(2L);
        when(projection.getCountyName()).thenReturn("Iasi");
        when(projection.getCityId()).thenReturn(3L);
        when(projection.getCityName()).thenReturn("Iasi");
        when(projection.getBrokerId()).thenReturn(10L);
        when(projection.getBrokerCode()).thenReturn("BR-10");
        when(projection.getBrokerName()).thenReturn("Broker 10");
        when(projection.getPolicyCount()).thenReturn(5L);
        when(projection.getTotalFinalPremium()).thenReturn(1500.5);

        when(policyRepository.reportByGeographyAndBroker(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(projection), PageRequest.of(0, 20), 1));

        Page<PolicyReportRow> result = service.policyReport(
                LocalDate.parse("2026-01-01"),
                LocalDate.parse("2026-01-31"),
                null,
                null,
                null,
                null,
                PageRequest.of(0, 20)
        );

        assertEquals(1, result.getTotalElements());
        PolicyReportRow row = result.getContent().getFirst();
        assertEquals("Romania", row.countryName());
        assertEquals(1500.5, row.totalFinalPremium());
    }
}