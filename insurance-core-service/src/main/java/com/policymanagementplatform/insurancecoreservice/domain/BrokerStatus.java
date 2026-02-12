package com.policymanagementplatform.insurancecoreservice.domain;

/*
LANES ADDED:
- BrokerStatus enum.
WHY:
- Part 2: admins can activate/deactivate brokers; status gates policy creation.
*/
public enum BrokerStatus {
    ACTIVE,
    INACTIVE
}
