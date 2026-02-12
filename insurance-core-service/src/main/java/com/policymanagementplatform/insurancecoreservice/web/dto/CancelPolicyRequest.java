package com.policymanagementplatform.insurancecoreservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
LANES ADDED:
- CancelPolicyRequest DTO.
WHY:
- Part 2: cancelling requires a reason; cancellation date recorded server-side.
*/
public record CancelPolicyRequest(
        @NotBlank @Size(max = 500) String reason
) {}
