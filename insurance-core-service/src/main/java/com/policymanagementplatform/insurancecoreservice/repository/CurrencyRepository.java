package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
LANES ADDED:
- CurrencyRepository.
WHY:
- Part 2: admin manages currencies; policy selects currency.
*/
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByCodeIgnoreCase(String code);
}
