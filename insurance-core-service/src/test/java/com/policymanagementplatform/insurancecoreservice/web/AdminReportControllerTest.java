package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.service.AdminReportService;
import com.policymanagementplatform.insurancecoreservice.web.dto.PolicyReportRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AdminReportControllerTest {

    private AdminReportService adminReportService;
    private AdminReportController controller;

    @BeforeEach
    void setUp() {
        adminReportService = mock(AdminReportService.class);
        controller = new AdminReportController(adminReportService);
    }

    @Test
    void policyReportShouldDelegateToServiceWithExpectedPageable() {
        LocalDate from = LocalDate.parse("2026-01-01");
        LocalDate to = LocalDate.parse("2026-01-31");
        Page<PolicyReportRow> expected = new PageImpl<>(List.of(
                new PolicyReportRow(1L, "Romania", 2L, "Cluj", 3L, "Cluj-Napoca", 4L, "BR-01", "Broker", 2, 1200)
        ), PageRequest.of(0, 20), 1);

        when(adminReportService.policyReport(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expected);

        Page<PolicyReportRow> result = controller.policyReport(from, to, 4L, 1L, 2L, 3L, 0, 20);

        assertSame(expected, result);
        verify(adminReportService).policyReport(
                eq(from),
                eq(to),
                eq(4L),
                eq(1L),
                eq(2L),
                eq(3L),
                argThat(p -> p.getPageNumber() == 0 && p.getPageSize() == 20)
        );
        verifyNoMoreInteractions(adminReportService);
    }
}