package com.policymanagementplatform.insurancecoreservice.web.dto;

import com.policymanagementplatform.insurancecoreservice.domain.FeeConfigurationType;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/*
LANES ADDED:
- CreateFeeRequest DTO.
WHY:
- Part 2: admin manages fee configurations including type, percentage, validity.
*/
public record CreateFeeRequest(
        @NotBlank @Size(max = 200) String name,
        @NotNull FeeConfigurationType type,
        double percentage,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveTo,
        boolean active
) {}
