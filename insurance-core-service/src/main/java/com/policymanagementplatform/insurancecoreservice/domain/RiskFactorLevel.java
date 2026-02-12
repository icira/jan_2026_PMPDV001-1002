package com.policymanagementplatform.insurancecoreservice.domain;

/*
LANES ADDED:
- RiskFactorLevel enum.
WHY:
- Part 2: risk adjustments can be defined by county, city, or building type.
*/
public enum RiskFactorLevel {
    COUNTRY,
    COUNTY,
    CITY,
    BUILDING_TYPE
}
