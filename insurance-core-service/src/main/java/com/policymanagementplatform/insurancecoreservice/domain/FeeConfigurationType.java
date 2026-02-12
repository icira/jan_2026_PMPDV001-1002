package com.policymanagementplatform.insurancecoreservice.domain;

/*
LANES ADDED:
- FeeConfigurationType enum.
WHY:
- Part 2 Q&A #2: Risk indicators in Building relate to FeeConfiguration (earthquake/flood).
- Q&A #3: BrokerCommission can exist globally; we also apply broker's own commissionPercentage.
*/
public enum FeeConfigurationType {
    BROKER_COMMISSION,
    ADMIN_FEE,
    RISK_ADJUSTMENT_EARTHQUAKE,
    RISK_ADJUSTMENT_FLOOD,
    RISK_ADJUSTMENT_GENERIC
}
