package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.Broker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
LANES ADDED:
- BrokerRepository for admin and policy flows.
WHY:
- Part 2: admin manages brokers; policy creation needs broker lookup.
*/
public interface BrokerRepository extends JpaRepository<Broker, Long> {
    Optional<Broker> findByBrokerCodeIgnoreCase(String brokerCode);
}
