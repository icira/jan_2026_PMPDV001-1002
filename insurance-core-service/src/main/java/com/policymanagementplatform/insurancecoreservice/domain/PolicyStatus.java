package com.policymanagementplatform.insurancecoreservice.domain;

/*
LANES ADDED:
- PolicyStatus enum.
WHY:
- Part 2: brokers list policies by status (Draft, Active, Expired, Cancelled).
*/
public enum PolicyStatus {
    DRAFT,
    ACTIVE,
    EXPIRED,
    CANCELLED
}
