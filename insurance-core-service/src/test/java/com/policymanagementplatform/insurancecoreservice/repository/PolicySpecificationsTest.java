package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.Policy;
import com.policymanagementplatform.insurancecoreservice.domain.PolicyStatus;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PolicySpecificationsTest {

    /*
    LANES ADDED:
    - Tests for PolicySpecifications predicates.
    WHY:
    - These specs are combined in PolicyService.search(...). If they are wrong, filtering is wrong.
    - We test both the "null filter => conjunction()" behavior and predicate wiring when values exist.
    */

    @Test
    void hasClientIdShouldReturnConjunctionWhenClientIdNull() {
        var spec = PolicySpecifications.hasClientId(null);

        Root<Policy> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate conj = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conj);

        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(conj, result);
        verify(cb).conjunction();
        verifyNoMoreInteractions(cb);
    }

    @Test
    void hasClientIdShouldBuildEqualityPredicateWhenClientIdProvided() {
        var spec = PolicySpecifications.hasClientId(10L);

        Root<Policy> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<Object> clientPath = mock(Path.class);
        Path<Object> clientIdPath = mock(Path.class);

        when(root.get("client")).thenReturn(clientPath);
        when(clientPath.get("id")).thenReturn(clientIdPath);

        Predicate predicate = mock(Predicate.class);
        when(cb.equal(clientIdPath, 10L)).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).equal(clientIdPath, 10L);
    }

    @Test
    void hasBrokerIdShouldReturnConjunctionWhenBrokerIdNull() {
        var spec = PolicySpecifications.hasBrokerId(null);

        Root<Policy> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate conj = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conj);

        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(conj, result);
        verify(cb).conjunction();
        verifyNoMoreInteractions(cb);
    }

    @Test
    void hasStatusShouldReturnConjunctionWhenStatusNull() {
        var spec = PolicySpecifications.hasStatus(null);

        Root<Policy> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate conj = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conj);

        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(conj, result);
        verify(cb).conjunction();
        verifyNoMoreInteractions(cb);
    }

    @Test
    void hasStatusShouldBuildEqualityPredicateWhenStatusProvided() {
        var spec = PolicySpecifications.hasStatus(PolicyStatus.ACTIVE);

        Root<Policy> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path<Object> statusPath = mock(Path.class);
        when(root.get("status")).thenReturn(statusPath);

        Predicate predicate = mock(Predicate.class);
        when(cb.equal(statusPath, PolicyStatus.ACTIVE)).thenReturn(predicate);

        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(predicate, result);
        verify(cb).equal(statusPath, PolicyStatus.ACTIVE);
    }

    @Test
    void hasPeriodWithinShouldReturnConjunctionWhenBothBoundsNull() {
        var spec = PolicySpecifications.hasPeriodWithin(null, null);

        Root<Policy> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate conj = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conj);

        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(conj, result);
        verify(cb).conjunction();
        verifyNoMoreInteractions(cb);
    }

    @Test
    void hasPeriodWithinShouldApplyOnlyStartBoundWhenOnlyStartProvided() {
        LocalDate from = LocalDate.of(2026, 1, 1);

        var spec = PolicySpecifications.hasPeriodWithin(from, null);

        Root<Policy> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate conj = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conj);

        Path startDatePath = mock(Path.class);
        when(root.get("startDate")).thenReturn(startDatePath);

        Predicate p1 = mock(Predicate.class);
        when(cb.greaterThanOrEqualTo(startDatePath, from)).thenReturn(p1);

        Predicate combined = mock(Predicate.class);
        when(cb.and(conj, p1)).thenReturn(combined);

        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(combined, result);
        verify(cb).greaterThanOrEqualTo(startDatePath, from);
        verify(cb).and(conj, p1);
    }

    @Test
    void hasPeriodWithinShouldApplyOnlyEndBoundWhenOnlyEndProvided() {
        LocalDate to = LocalDate.of(2026, 12, 31);

        var spec = PolicySpecifications.hasPeriodWithin(null, to);

        Root<Policy> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate conj = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conj);

        Path endDatePath = mock(Path.class);
        when(root.get("endDate")).thenReturn(endDatePath);

        Predicate p1 = mock(Predicate.class);
        when(cb.lessThanOrEqualTo(endDatePath, to)).thenReturn(p1);

        Predicate combined = mock(Predicate.class);
        when(cb.and(conj, p1)).thenReturn(combined);

        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(combined, result);
        verify(cb).lessThanOrEqualTo(endDatePath, to);
        verify(cb).and(conj, p1);
    }

    @Test
    void hasPeriodWithinShouldApplyBothBoundsWhenBothProvided() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 12, 31);

        var spec = PolicySpecifications.hasPeriodWithin(from, to);

        Root<Policy> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate conj = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(conj);

        Path startDatePath = mock(Path.class);
        Path endDatePath = mock(Path.class);
        when(root.get("startDate")).thenReturn(startDatePath);
        when(root.get("endDate")).thenReturn(endDatePath);

        Predicate pStart = mock(Predicate.class);
        Predicate pEnd = mock(Predicate.class);

        when(cb.greaterThanOrEqualTo(startDatePath, from)).thenReturn(pStart);
        when(cb.lessThanOrEqualTo(endDatePath, to)).thenReturn(pEnd);

        Predicate combined1 = mock(Predicate.class);
        Predicate combined2 = mock(Predicate.class);
        when(cb.and(conj, pStart)).thenReturn(combined1);
        when(cb.and(combined1, pEnd)).thenReturn(combined2);

        Predicate result = spec.toPredicate(root, query, cb);

        assertSame(combined2, result);
        verify(cb).greaterThanOrEqualTo(startDatePath, from);
        verify(cb).lessThanOrEqualTo(endDatePath, to);
        verify(cb).and(conj, pStart);
        verify(cb).and(combined1, pEnd);
    }
}
