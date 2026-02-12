package com.policymanagementplatform.insurancecoreservice.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateClientRequest(
        @NotBlank @Size(max = 200) String name,
        @Email @Size(max = 200) String email,
        @Size(max = 50) String phone,
        @Size(max = 300) String primaryAddress
) { }
