package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.Policy;
import com.policymanagementplatform.insurancecoreservice.domain.PolicyStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/*
LANES ADDED:
- Replaced startDateBetween(from,to) with periodWithin(startFrom,endTo).
WHY:
- Part 2 API supports startDate and endDate filters for listing policies.
- Correct semantics for a policy "period":
  * startDate >= startFrom (if provided)
  * endDate   <= endTo     (if provided)
- Supports only one bound (only startDate or only endDate) as well.
*/
public final class PolicySpecifications {

    private PolicySpecifications() { }

    public static Specification<Policy> hasClientId(Long clientId) {
        return (root, query, cb) ->
                clientId == null ? cb.conjunction() : cb.equal(root.get("client").get("id"), clientId);
    }

    public static Specification<Policy> hasBrokerId(Long brokerId) {
        return (root, query, cb) ->
                brokerId == null ? cb.conjunction() : cb.equal(root.get("broker").get("id"), brokerId);
    }

    public static Specification<Policy> hasStatus(PolicyStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    /**
     * Period filter:
     * - startFrom: include policies with startDate >= startFrom
     * - endTo:     include policies with endDate   <= endTo
     *
     * Each bound is optional.
     */
    public static Specification<Policy> periodWithin(LocalDate startFrom, LocalDate endTo) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (startFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("startDate"), startFrom));
            }
            if (endTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("endDate"), endTo));
            }

            return predicate;
        };
    }
}
