package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.Policy;
import com.policymanagementplatform.insurancecoreservice.domain.PolicyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/*
LANES ADDED:
- JpaSpecificationExecutor for flexible filtering.
WHY:
- Part 2: brokers list policies by client/broker/status/date range.
*/
public interface PolicyRepository extends JpaRepository<Policy, Long>, JpaSpecificationExecutor<Policy> {
    Optional<Policy> findByPolicyNumber(String policyNumber);
    boolean existsByCurrency_IdAndStatus(Long currencyId, PolicyStatus status);

    @Modifying
    @Query("""
            update Policy p
            set p.status = com.policymanagementplatform.insurancecoreservice.domain.PolicyStatus.EXPIRED
            where p.status = com.policymanagementplatform.insurancecoreservice.domain.PolicyStatus.ACTIVE
              and p.endDate < :today
            """)
    int expireActivePolicies(@Param("today") java.time.LocalDate today);

    @Query(value = """
            select
                ctry.id as countryId,
                ctry.name as countryName,
                cnty.id as countyId,
                cnty.name as countyName,
                city.id as cityId,
                city.name as cityName,
                broker.id as brokerId,
                broker.broker_code as brokerCode,
                broker.name as brokerName,
                count(p.id) as policyCount,
                coalesce(sum(p.final_premium), 0) as totalFinalPremium
            from policies p
            join buildings b on b.id = p.building_id
            join cities city on city.id = b.city_id
            join counties cnty on cnty.id = city.county_id
            join countries ctry on ctry.id = cnty.country_id
            join brokers broker on broker.id = p.broker_id
            where (:startDate is null or p.start_date >= :startDate)
              and (:endDate is null or p.end_date <= :endDate)
              and (:brokerId is null or broker.id = :brokerId)
              and (:countryId is null or ctry.id = :countryId)
              and (:countyId is null or cnty.id = :countyId)
              and (:cityId is null or city.id = :cityId)
            group by
                ctry.id, ctry.name,
                cnty.id, cnty.name,
                city.id, city.name,
                broker.id, broker.broker_code, broker.name
            """,
            countQuery = """
                    select count(*)
                    from (
                        select 1
                        from policies p
                        join buildings b on b.id = p.building_id
                        join cities city on city.id = b.city_id
                        join counties cnty on cnty.id = city.county_id
                        join countries ctry on ctry.id = cnty.country_id
                        join brokers broker on broker.id = p.broker_id
                        where (:startDate is null or p.start_date >= :startDate)
                          and (:endDate is null or p.end_date <= :endDate)
                          and (:brokerId is null or broker.id = :brokerId)
                          and (:countryId is null or ctry.id = :countryId)
                          and (:countyId is null or cnty.id = :countyId)
                          and (:cityId is null or city.id = :cityId)
                        group by
                            ctry.id, ctry.name,
                            cnty.id, cnty.name,
                            city.id, city.name,
                            broker.id, broker.broker_code, broker.name
                    ) x
                    """,
            nativeQuery = true)
    Page<PolicyReportProjection> reportByGeographyAndBroker(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("brokerId") Long brokerId,
            @Param("countryId") Long countryId,
            @Param("countyId") Long countyId,
            @Param("cityId") Long cityId,
            Pageable pageable
    );
}