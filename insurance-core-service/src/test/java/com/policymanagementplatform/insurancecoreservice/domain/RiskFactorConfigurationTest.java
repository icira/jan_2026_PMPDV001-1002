package com.policymanagementplatform.insurancecoreservice.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RiskFactorConfigurationTest {

    /*
    LANES ADDED:
    - Unit tests for RiskFactorConfiguration update.
    WHY:
    - Admin can change adjustment percentage and active flag.
    */

    @Test
    void updateShouldMutateAdjustmentAndActiveFlag() {
        RiskFactorConfiguration cfg = new RiskFactorConfiguration(RiskFactorLevel.CITY, 10L, 3.0, true);

        cfg.update(5.5, false);

        assertEquals(5.5, cfg.getAdjustmentPercentage(), 0.0001);
        assertFalse(cfg.isActive());
        assertEquals(RiskFactorLevel.CITY, cfg.getLevel());
        assertEquals(10L, cfg.getReferenceId());
    }
}
