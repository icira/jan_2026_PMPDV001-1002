package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.*;
import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

/*
LANES ADDED:
- PolicyService implements Part 2 policy operations (create draft, activate, cancel, search).
WHY:
- Part 2: keep business logic out of controllers; enforce rules centrally.
*/
@Service
public class PolicyService {

    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);

    private final PolicyRepository policyRepository;
    private final ClientRepository clientRepository;
    private final BuildingRepository buildingRepository;
    private final BrokerRepository brokerRepository;
    private final CurrencyRepository currencyRepository;
    private final PolicyCalculationService calculationService;

    public PolicyService(PolicyRepository policyRepository,
                         ClientRepository clientRepository,
                         BuildingRepository buildingRepository,
                         BrokerRepository brokerRepository,
                         CurrencyRepository currencyRepository,
                         PolicyCalculationService calculationService) {
        this.policyRepository = policyRepository;
        this.clientRepository = clientRepository;
        this.buildingRepository = buildingRepository;
        this.brokerRepository = brokerRepository;
        this.currencyRepository = currencyRepository;
        this.calculationService = calculationService;
    }

    @Transactional
    public Policy createDraft(Long clientId,
                              Long buildingId,
                              Long currencyId,
                              double basePremium,
                              LocalDate startDate,
                              LocalDate endDate,
                              Long brokerId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new NotFoundException("Building not found"));

        if (!building.getOwner().getId().equals(clientId)) {
            throw new ConflictException("Building does not belong to client");
        }

        Broker broker = brokerRepository.findById(brokerId)
                .orElseThrow(() -> new NotFoundException("Broker not found"));

        if (broker.getStatus() != BrokerStatus.ACTIVE) {
            throw new ConflictException("Broker is not active");
        }

        Currency currency = currencyRepository.findById(currencyId)
                .orElseThrow(() -> new NotFoundException("Currency not found"));

        if (!currency.isActive()) {
            throw new ConflictException("Currency is not active");
        }

        if (basePremium <= 0) {
            throw new ConflictException("Base premium must be positive");
        }

        if (endDate.isBefore(startDate)) {
            throw new ConflictException("End date must be on/after start date");
        }

        double finalPremium = calculationService.calculateFinalPremium(basePremium, broker, building, startDate);

        String policyNumber = UUID.randomUUID().toString();

        Policy policy = new Policy(policyNumber, client, building, broker, currency, startDate, endDate, basePremium, finalPremium);
        Policy saved = policyRepository.save(policy);

        log.info("Policy created id={} policyNumber={} brokerId={} clientId={}", saved.getId(), saved.getPolicyNumber(), brokerId, clientId);
        return saved;
    }

    @Transactional
    public Policy activate(Long policyId, LocalDate today) {
        markExpiredPolicies(today);

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new NotFoundException("Policy not found"));

        if (policy.getStartDate().isBefore(today)) {
            throw new ConflictException("Start date is in the past; cannot activate policy");
        }

        policy.activate();
        log.info("Policy activated id={} policyNumber={}", policy.getId(), policy.getPolicyNumber());
        return policy;
    }

    @Transactional
    public Policy cancel(Long policyId, String reason, LocalDate cancellationDate) {
        markExpiredPolicies(cancellationDate);

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new NotFoundException("Policy not found"));

        if (reason == null || reason.isBlank()) {
            throw new ConflictException("Cancellation reason is required");
        }

        policy.cancel(reason, cancellationDate);
        log.info("Policy cancelled id={} policyNumber={} reason={}", policy.getId(), policy.getPolicyNumber(), reason);
        return policy;
    }

    @Transactional
    public Policy get(Long policyId) {
        markExpiredPolicies(LocalDate.now());
        return policyRepository.findById(policyId)
                .orElseThrow(() -> new NotFoundException("Policy not found"));
    }

    @Transactional
    public Page<Policy> search(Long clientId,
                               Long brokerId,
                               PolicyStatus status,
                               LocalDate startDateFrom,
                               LocalDate startDateTo,
                               Pageable pageable) {

        markExpiredPolicies(LocalDate.now());

        Specification<Policy> spec = Specification.where(PolicySpecifications.hasClientId(clientId))
                .and(PolicySpecifications.hasBrokerId(brokerId))
                .and(PolicySpecifications.hasStatus(status))
                .and(PolicySpecifications.periodWithin(startDateFrom, startDateTo));

        return policyRepository.findAll(spec, pageable);
    }

    private void markExpiredPolicies(LocalDate today) {
        int expired = policyRepository.expireActivePolicies(today);
        if (expired > 0) {
            log.info("Policies auto-expired count={} at date={}", expired, today);
        }
    }
}
