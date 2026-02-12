package com.policymanagementplatform.insurancecoreservice.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrokerTest {

    /*
    LANES ADDED:
    - Unit tests for Broker entity behavior.
    WHY:
    - Brokers can be activated/deactivated and updated by admins.
    */

    @Test
    void activateShouldSetStatusActive() {
        Broker broker = new Broker("BR-1", "Name", "a@b.com", "123", 5.0, BrokerStatus.INACTIVE);

        broker.activate();

        assertEquals(BrokerStatus.ACTIVE, broker.getStatus());
    }

    @Test
    void deactivateShouldSetStatusInactive() {
        Broker broker = new Broker("BR-1", "Name", "a@b.com", "123", 5.0, BrokerStatus.ACTIVE);

        broker.deactivate();

        assertEquals(BrokerStatus.INACTIVE, broker.getStatus());
    }

    @Test
    void updateShouldReplaceEditableFieldsButNotBrokerCode() {
        Broker broker = new Broker("BR-1", "Name", "a@b.com", "123", 5.0, BrokerStatus.ACTIVE);

        broker.update("New", "new@b.com", "999", 7.5);

        assertEquals("BR-1", broker.getBrokerCode());
        assertEquals("New", broker.getName());
        assertEquals("new@b.com", broker.getEmail());
        assertEquals("999", broker.getPhone());
        assertEquals(7.5, broker.getCommissionPercentage(), 0.0001);
    }
}
