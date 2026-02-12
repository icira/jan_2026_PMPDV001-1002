package com.policymanagementplatform.insurancecoreservice.web;

import com.policymanagementplatform.insurancecoreservice.service.AdminReportService;
import com.policymanagementplatform.insurancecoreservice.web.dto.PolicyReportRow;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/*
LANES ADDED:
- Admin report endpoint for policy statistics.
WHY:
- Part 2 asks admins to view policy count + total value grouped by geography and broker in a period.
*/
@RestController
@RequestMapping("/api/admin/reports")
public class AdminReportController {

    private final AdminReportService adminReportService;

    public AdminReportController(AdminReportService adminReportService) {
        this.adminReportService = adminReportService;
    }

    @GetMapping("/policies")
    public Page<PolicyReportRow> policyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long brokerId,
            @RequestParam(required = false) Long countryId,
            @RequestParam(required = false) Long countyId,
            @RequestParam(required = false) Long cityId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("countryName", "countyName", "cityName", "brokerName").ascending());
        return adminReportService.policyReport(startDate, endDate, brokerId, countryId, countyId, cityId, pageable);
    }
}