package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.Currency;
import com.policymanagementplatform.insurancecoreservice.domain.PolicyStatus;
import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.CurrencyRepository;
import com.policymanagementplatform.insurancecoreservice.repository.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
LANES ADDED:
- CurrencyAdminService with "cannot deactivate if used in active policies" rule.
WHY:
- Part 2: admins can deactivate currency but must not break active policies; at minimum, prevent future use.
*/
@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final PolicyRepository policyRepository;

    public CurrencyService(CurrencyRepository currencyRepository, PolicyRepository policyRepository) {
        this.currencyRepository = currencyRepository;
        this.policyRepository = policyRepository;
    }

    @Transactional(readOnly = true)
    public List<Currency> list() {
        return currencyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Currency get(Long id) {
        return currencyRepository.findById(id).orElseThrow(() -> new NotFoundException("Currency not found"));
    }

    @Transactional
    public Currency create(String code, String name, double exchangeRateToBase, boolean active) {
        currencyRepository.findByCodeIgnoreCase(code).ifPresent(existing -> {
            throw new ConflictException("Currency code already exists");
        });
        return currencyRepository.save(new Currency(code, name, exchangeRateToBase, active));
    }

    @Transactional
    public Currency update(Long id, String name, double exchangeRateToBase, boolean active) {
        Currency currency = get(id);

        // Business safety: block deactivation if currency is used by ACTIVE policies.
        if (!active && policyRepository.existsByCurrency_IdAndStatus(id, PolicyStatus.ACTIVE)) {
            throw new ConflictException("Cannot deactivate currency used by active policies");
        }

        currency.update(name, exchangeRateToBase, active);
        return currency;
    }
}
