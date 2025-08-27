package com.catalis.domain.people.core.service.impl;

import com.catalis.common.domain.events.inbound.EventListener;
import com.catalis.common.domain.events.outbound.DomainEventPublisher;
import com.catalis.common.domain.events.outbound.EventPublisher;
import com.catalis.common.domain.events.properties.DomainEventsProperties;
import com.catalis.domain.people.core.orchestrator.RegisterCustomerOrchestrator;
import com.catalis.domain.people.core.service.PersonCommandService;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterCustomerCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyStatusEntryCommand;
import com.catalis.transactionalengine.core.SagaContext;
import com.catalis.transactionalengine.core.SagaResult;
import com.catalis.transactionalengine.engine.ExpandEach;
import com.catalis.transactionalengine.engine.SagaEngine;
import com.catalis.transactionalengine.engine.StepInputs;
import com.catalis.transactionalengine.registry.SagaBuilder;
import com.catalis.transactionalengine.registry.SagaDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Implementation of PersonCommandService that orchestrates customer registration
 * using the Saga pattern for distributed transaction management.
 * 
 * This service coordinates multiple registration steps through the RegisterCustomerOrchestrator,
 * ensuring data consistency across various customer-related entities including party information,
 * personal details, addresses, contacts, and relationships.
 */
@Service
public class PersonCommandServiceImpl implements PersonCommandService {

    private final SagaEngine engine;

    @Autowired
    public PersonCommandServiceImpl(SagaEngine engine) {
        this.engine = engine;
    }

    @Override
    public Mono<SagaResult> register(RegisterCustomerCommand command) {

        StepInputs inputs = StepInputs.builder()
                .forStep(RegisterCustomerOrchestrator::registerParty, command.party())
                .forStep(RegisterCustomerOrchestrator::registerNaturalPerson, command.naturalPerson())
                .forStep(RegisterCustomerOrchestrator::registerLegalPerson, command.legalPerson())
                .forStep(RegisterCustomerOrchestrator::registerStatusEntry, ExpandEach.of(command.statusHistory()))
                .forStep(RegisterCustomerOrchestrator::registerPep, command.pep())
                .forStep(RegisterCustomerOrchestrator::registerIdentityDocument, ExpandEach.of(command.identityDocuments()))
                .forStep(RegisterCustomerOrchestrator::registerAddress, ExpandEach.of(command.addresses()))
                .forStep(RegisterCustomerOrchestrator::registerEmail, ExpandEach.of(command.emails()))
                .forStep(RegisterCustomerOrchestrator::registerPhone, ExpandEach.of(command.phones()))
                .forStep(RegisterCustomerOrchestrator::registerEconomicActivityLink, ExpandEach.of(command.economicActivities()))
                .forStep(RegisterCustomerOrchestrator::registerConsent, ExpandEach.of(command.consents()))
                .forStep(RegisterCustomerOrchestrator::registerPartyProvider, ExpandEach.of(command.providers()))
                .forStep(RegisterCustomerOrchestrator::registerPartyRelationship, ExpandEach.of(command.relationships()))
                .forStep(RegisterCustomerOrchestrator::registerPartyGroupMembership, ExpandEach.of(command.groupMemberships()))
                .build();

        return engine.execute(RegisterCustomerOrchestrator.class, inputs);
    }

    @Override
    public Mono<Void> updateName(Long customerId, String newName) {
        // TODO: Implement name update logic
        return Mono.empty();
    }

    // Address operations
    @Override
    public Mono<Void> addAddress(Long customerId, Object addressData) {
        // TODO: Implement add address logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> updateAddress(Long customerId, Long addressId, Object addressData) {
        // TODO: Implement update address logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> removeAddress(Long customerId, Long addressId) {
        // TODO: Implement remove address logic
        return Mono.empty();
    }

    // Email operations
    @Override
    public Mono<Void> addEmail(Long customerId, Object emailData) {
        // TODO: Implement add email logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> removeEmail(Long customerId, Long emailId) {
        // TODO: Implement remove email logic
        return Mono.empty();
    }

    // Phone operations
    @Override
    public Mono<Void> addPhone(Long customerId, Object phoneData) {
        // TODO: Implement add phone logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> removePhone(Long customerId, Long phoneId) {
        // TODO: Implement remove phone logic
        return Mono.empty();
    }

    // Preferred channel operations
    @Override
    public Mono<Void> setPreferredChannel(Long customerId, Object channelData) {
        // TODO: Implement set preferred channel logic
        return Mono.empty();
    }

    // Authorized signatory operations
    @Override
    public Mono<Void> addAuthorizedSignatory(Long customerId, Object signatoryData) {
        // TODO: Implement add authorized signatory logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> removeAuthorizedSignatory(Long customerId, Long partyId) {
        // TODO: Implement remove authorized signatory logic
        return Mono.empty();
    }

    // Status operations
    @Override
    public Mono<Void> markDormant(Long customerId) {
        // TODO: Implement mark dormant logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> reactivate(Long customerId) {
        // TODO: Implement reactivate logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> markDeceased(Long customerId) {
        // TODO: Implement mark deceased logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> requestClosure(Long customerId) {
        // TODO: Implement request closure logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> confirmClosure(Long customerId) {
        // TODO: Implement confirm closure logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> mergeWith(Long customerId, Object mergeData) {
        // TODO: Implement merge with logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> splitFrom(Long customerId, Object splitData) {
        // TODO: Implement split from logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> lockProfile(Long customerId) {
        // TODO: Implement lock profile logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> unlockProfile(Long customerId) {
        // TODO: Implement unlock profile logic
        return Mono.empty();
    }
}