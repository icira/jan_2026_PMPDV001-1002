package com.policymanagementplatform.insurancecoreservice.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyTest {

    /*
    LANES ADDED:
    - Unit tests for Currency entity update.
    WHY:
    - Admin can update and deactivate currencies; update must mutate fields.
    */

    @Test
    void updateShouldModifyNameRateAndActiveFlag() {
        Currency currency = new Currency("RON", "Leu", 1.0, true);

        currency.update("Leu v2", 2.5, false);

        assertEquals("RON", currency.getCode());
        assertEquals("Leu v2", currency.getName());
        assertEquals(2.5, currency.getExchangeRateToBase(), 0.0001);
        assertFalse(currency.isActive());
    }
}
