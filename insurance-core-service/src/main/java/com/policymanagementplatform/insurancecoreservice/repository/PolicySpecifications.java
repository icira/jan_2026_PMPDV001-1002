package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.Policy;
import com.policymanagementplatform.insurancecoreservice.domain.PolicyStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Factory class for reusable {@link Specification} instances used to filter {@link Policy}
 * entities in repository queries.
 * <p>
 * Each method returns a specification that can be composed with others. When a filter argument
 * is {@code null}, the specification resolves to a conjunction (an always-true predicate), so it
 * can be safely included in chained search criteria.
 */
public final class PolicySpecifications {
    /**
     * Utility class constructor.
     *
     * <p>Private to prevent instantiation.</p>
     */
    private PolicySpecifications() {
    }

    /**
     * Builds a specification that matches policies belonging to a given client.
     *
     * @param clientId client identifier to match; when {@code null}, no client filter is applied
     * @return specification for client filtering, or an always-true predicate if {@code clientId}
     * is {@code null}
     */
    public static Specification<Policy> hasClientId(Long clientId) {
        return (root, query, cb) ->
                clientId == null ? cb.conjunction() : cb.equal(root.get("client").get("id"), clientId);
    }

    /**
     * Builds a specification that matches policies assigned to a given broker.
     *
     * @param brokerId broker identifier to match; when {@code null}, no broker filter is applied
     * @return specification for broker filtering, or an always-true predicate if {@code brokerId}
     * is {@code null}
     */
    public static Specification<Policy> hasBrokerId(Long brokerId) {
        return (root, query, cb) ->
                brokerId == null ? cb.conjunction() : cb.equal(root.get("broker").get("id"), brokerId);
    }

    /**
     * Builds a specification that matches policies by status.
     *
     * @param status status to match; when {@code null}, no status filter is applied
     * @return specification for status filtering, or an always-true predicate if {@code status}
     * is {@code null}
     */
    public static Specification<Policy> hasStatus(PolicyStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    /**
     * Builds a specification that filters policies by a period window.
     * <ul>
     *     <li>If {@code startDateFrom} is provided, policies must satisfy
     *     {@code startDate >= startDateFrom}.</li>
     *     <li>If {@code endDateTo} is provided, policies must satisfy
     *     {@code endDate <= endDateTo}.</li>
     * </ul>
     * Both bounds are optional and are combined with logical {@code AND} when present.
     *
     * @param startDateFrom inclusive lower bound for policy start date; optional
     * @param endDateTo     inclusive upper bound for policy end date; optional
     * @return specification for period filtering, or an always-true predicate when both bounds are
     * {@code null}
     */
    public static Specification<Policy> hasPeriodWithin(LocalDate startDateFrom, LocalDate endDateTo) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (startDateFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("startDate"), startDateFrom));
            }
            if (endDateTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("endDate"), endDateTo));
            }

            return predicate;
        };
    }
}
