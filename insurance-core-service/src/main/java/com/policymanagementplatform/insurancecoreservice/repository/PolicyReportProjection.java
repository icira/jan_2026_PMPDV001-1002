package com.policymanagementplatform.insurancecoreservice.repository;

public interface PolicyReportProjection {
    Long getCountryId();
    String getCountryName();
    Long getCountyId();
    String getCountyName();
    Long getCityId();
    String getCityName();
    Long getBrokerId();
    String getBrokerCode();
    String getBrokerName();
    Long getPolicyCount();
    Double getTotalFinalPremium();
}