package com.policymanagementplatform.insurancecoreservice.web.dto;

import com.policymanagementplatform.insurancecoreservice.domain.BrokerStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/*
LANES ADDED (Part 2):
- DTO used by AdminBrokerController to create brokers.
WHY:
- Keeps validation out of controllers
- Enforces Part 2 constraints at API boundary
*/
public record CreateBrokerRequest(
        @NotBlank @Size(max = 50) String brokerCode,
        @NotBlank @Size(max = 200) String name,
        @Email @Size(max = 200) String email,
        @Size(max = 50) String phone,
        @PositiveOrZero double commissionPercentage,
        @NotNull BrokerStatus initialStatus
) {}
