package com.policymanagementplatform.insurancecoreservice.repository;

import com.policymanagementplatform.insurancecoreservice.domain.ClientIdentifierChange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientIdentifierChangeRepository extends JpaRepository<ClientIdentifierChange, Long> { }
