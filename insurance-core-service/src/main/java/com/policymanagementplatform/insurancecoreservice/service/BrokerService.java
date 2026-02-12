package com.policymanagementplatform.insurancecoreservice.service;

import com.policymanagementplatform.insurancecoreservice.domain.Broker;
import com.policymanagementplatform.insurancecoreservice.domain.BrokerStatus;
import com.policymanagementplatform.insurancecoreservice.exceptions.ConflictException;
import com.policymanagementplatform.insurancecoreservice.exceptions.NotFoundException;
import com.policymanagementplatform.insurancecoreservice.repository.BrokerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
LANES ADDED:
- BrokerAdminService for admin CRUD + activate/deactivate.
WHY:
- Part 2: admins manage brokers; status controls policy creation.
*/
@Service
public class BrokerService {

    private final BrokerRepository brokerRepository;

    public BrokerService(BrokerRepository brokerRepository) {
        this.brokerRepository = brokerRepository;
    }

    @Transactional(readOnly = true)
    public Page<Broker> list(Pageable pageable) {
        return brokerRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Broker get(Long id) {
        return brokerRepository.findById(id).orElseThrow(() -> new NotFoundException("Broker not found"));
    }

    @Transactional
    public Broker create(String brokerCode, String name, String email, String phone, double commissionPercentage, BrokerStatus status) {
        brokerRepository.findByBrokerCodeIgnoreCase(brokerCode).ifPresent(existing -> {
            throw new ConflictException("Broker code already exists");
        });

        Broker broker = new Broker(brokerCode, name, email, phone, commissionPercentage, status);
        return brokerRepository.save(broker);
    }

    @Transactional
    public Broker update(Long id, String name, String email, String phone, double commissionPercentage) {
        Broker broker = get(id);
        broker.update(name, email, phone, commissionPercentage);
        return broker;
    }

    @Transactional
    public Broker changeStatus(Long id, BrokerStatus status) {
        Broker broker = get(id);
        if (status == BrokerStatus.ACTIVE) {
            broker.activate();
        } else {
            broker.deactivate();
        }
        return broker;
    }
}
