package com.policymanagementplatform.insurancecoreservice.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolicyTest {

    /*
    LANES ADDED:
    - Additional tests for new domain-level validation:
      * cancel() rejects blank reason
      * cancel() rejects null cancellation date
    WHY:
    - We added defensive checks in Policy.cancel(...), so we test them for correctness and coverage.
    */

    @Test
    void constructorShouldDefaultStatusToDraft() {
        Policy policy = new Policy(
                "PN-1",
                mock(Client.class),
                mock(Building.class),
                mock(Broker.class),
                mock(Currency.class),
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 3, 1),
                1000.0,
                1200.0
        );

        assertEquals(PolicyStatus.DRAFT, policy.getStatus());
        assertNull(policy.getCancellationReason());
        assertNull(policy.getCancellationDate());
    }

    @Test
    void activateShouldMoveFromDraftToActive() {
        Policy policy = new Policy(
                "PN-1",
                mock(Client.class),
                mock(Building.class),
                mock(Broker.class),
                mock(Currency.class),
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 3, 1),
                1000.0,
                1200.0
        );

        policy.activate();
        assertEquals(PolicyStatus.ACTIVE, policy.getStatus());
    }

    @Test
    void activateShouldThrowWhenNotInDraft() {
        Policy policy = new Policy(
                "PN-1",
                mock(Client.class),
                mock(Building.class),
                mock(Broker.class),
                mock(Currency.class),
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 3, 1),
                1000.0,
                1200.0
        );

        policy.activate();

        assertThrows(IllegalStateException.class, policy::activate);
    }

    @Test
    void cancelShouldMoveFromActiveToCancelledAndStoreReasonAndDate() {
        Policy policy = new Policy(
                "PN-1",
                mock(Client.class),
                mock(Building.class),
                mock(Broker.class),
                mock(Currency.class),
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 3, 1),
                1000.0,
                1200.0
        );

        policy.activate();

        LocalDate cancellationDate = LocalDate.of(2026, 4, 1);
        policy.cancel("Client request", cancellationDate);

        assertEquals(PolicyStatus.CANCELLED, policy.getStatus());
        assertEquals("Client request", policy.getCancellationReason());
        assertEquals(cancellationDate, policy.getCancellationDate());
    }

    @Test
    void cancelShouldThrowWhenNotActive() {
        Policy policy = new Policy(
                "PN-1",
                mock(Client.class),
                mock(Building.class),
                mock(Broker.class),
                mock(Currency.class),
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 3, 1),
                1000.0,
                1200.0
        );

        assertThrows(IllegalStateException.class, () -> policy.cancel("reason", LocalDate.of(2026, 4, 1)));
    }

    @Test
    void cancelShouldThrowWhenReasonBlank() {
        Policy policy = new Policy(
                "PN-1",
                mock(Client.class),
                mock(Building.class),
                mock(Broker.class),
                mock(Currency.class),
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 3, 1),
                1000.0,
                1200.0
        );

        policy.activate();

        assertThrows(IllegalArgumentException.class, () ->
                policy.cancel("   ", LocalDate.of(2026, 4, 1))
        );
    }

    @Test
    void cancelShouldThrowWhenCancellationDateNull() {
        Policy policy = new Policy(
                "PN-1",
                mock(Client.class),
                mock(Building.class),
                mock(Broker.class),
                mock(Currency.class),
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2027, 3, 1),
                1000.0,
                1200.0
        );

        policy.activate();

        assertThrows(IllegalArgumentException.class, () ->
                policy.cancel("Client request", null)
        );
    }
}
