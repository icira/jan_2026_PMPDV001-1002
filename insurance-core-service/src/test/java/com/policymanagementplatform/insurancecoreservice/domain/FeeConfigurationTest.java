package com.policymanagementplatform.insurancecoreservice.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FeeConfigurationTest {

    /*
    LANES ADDED:
    - Unit tests for FeeConfiguration helper logic.
    WHY:
    - isEffectiveOn(date) is used to reason about date ranges; we cover boundaries.
    */

    @Test
    void isEffectiveOnShouldReturnFalseWhenInactive() {
        FeeConfiguration fee = new FeeConfiguration(
                "Admin",
                FeeConfigurationType.ADMIN_FEE,
                2.0,
                LocalDate.of(2026, 1, 1),
                null,
                false
        );

        assertFalse(fee.isEffectiveOn(LocalDate.of(2026, 2, 10)));
    }

    @Test
    void isEffectiveOnShouldReturnFalseWhenBeforeEffectiveFrom() {
        FeeConfiguration fee = new FeeConfiguration(
                "Admin",
                FeeConfigurationType.ADMIN_FEE,
                2.0,
                LocalDate.of(2026, 2, 11),
                null,
                true
        );

        assertFalse(fee.isEffectiveOn(LocalDate.of(2026, 2, 10)));
    }

    @Test
    void isEffectiveOnShouldReturnTrueWhenWithinOpenEndedRange() {
        FeeConfiguration fee = new FeeConfiguration(
                "Admin",
                FeeConfigurationType.ADMIN_FEE,
                2.0,
                LocalDate.of(2026, 1, 1),
                null,
                true
        );

        assertTrue(fee.isEffectiveOn(LocalDate.of(2026, 2, 10)));
    }

    @Test
    void isEffectiveOnShouldRespectEffectiveToInclusive() {
        FeeConfiguration fee = new FeeConfiguration(
                "Admin",
                FeeConfigurationType.ADMIN_FEE,
                2.0,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 2, 10),
                true
        );

        assertTrue(fee.isEffectiveOn(LocalDate.of(2026, 2, 10)));
        assertFalse(fee.isEffectiveOn(LocalDate.of(2026, 2, 11)));
    }

    @Test
    void updateShouldMutatePercentageDatesAndActiveFlag() {
        FeeConfiguration fee = new FeeConfiguration(
                "Admin",
                FeeConfigurationType.ADMIN_FEE,
                2.0,
                LocalDate.of(2026, 1, 1),
                null,
                true
        );

        fee.update(3.5, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 12, 31), false);

        assertEquals(3.5, fee.getPercentage(), 0.0001);
        assertEquals(LocalDate.of(2026, 2, 1), fee.getEffectiveFrom());
        assertEquals(LocalDate.of(2026, 12, 31), fee.getEffectiveTo());
        assertFalse(fee.isActive());
    }
}
