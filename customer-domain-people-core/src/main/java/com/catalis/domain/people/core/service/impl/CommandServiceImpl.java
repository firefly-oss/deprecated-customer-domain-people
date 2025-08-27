package com.catalis.domain.people.core.service.impl;

import com.catalis.domain.people.core.orchestrator.address.AddAddressOrchestrator;
import com.catalis.domain.people.core.orchestrator.customer.RegisterCustomerOrchestrator;
import com.catalis.domain.people.core.service.CommandService;
import com.catalis.domain.people.interfaces.dto.commands.RegisterAddressCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterCustomerCommand;
import com.catalis.transactionalengine.core.SagaResult;
import com.catalis.transactionalengine.engine.ExpandEach;
import com.catalis.transactionalengine.engine.SagaEngine;
import com.catalis.transactionalengine.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Implementation of PersonCommandService that orchestrates customer registration
 * using the Saga pattern for distributed transaction management.
 * 
 * This service coordinates multiple registration steps through the RegisterCustomerOrchestrator,
 * ensuring data consistency across various customer-related entities including party information,
 * personal details, addresses, contacts, and relationships.
 */
@Service
public class CommandServiceImpl implements CommandService {

    private final SagaEngine engine;

    @Autowired
    public CommandServiceImpl(SagaEngine engine) {
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
    public Mono<Void> updateName(Long partyId, String newName) {
        // TODO: Implement name update logic
        return Mono.empty();
    }

    // Address operations
    @Override
    public Mono<SagaResult> addAddress(Long partyId, RegisterAddressCommand addressCommand) {
        StepInputs inputs = StepInputs.builder()
                .forStep(AddAddressOrchestrator::registerAddress, addressCommand.withPartyId(partyId))
                .build();

        return engine.execute(AddAddressOrchestrator.class, inputs);
    }

    @Override
    public Mono<Void> updateAddress(Long partyId, Long addressId, Object addressData) {
        // TODO: Implement update address logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> removeAddress(Long partyId, Long addressId) {
        // TODO: Implement remove address logic
        return Mono.empty();
    }

    // Email operations
    @Override
    public Mono<Void> addEmail(Long partyId, Object emailData) {
        // TODO: Implement add email logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> removeEmail(Long partyId, Long emailId) {
        // TODO: Implement remove email logic
        return Mono.empty();
    }

    // Phone operations
    @Override
    public Mono<Void> addPhone(Long partyId, Object phoneData) {
        // TODO: Implement add phone logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> removePhone(Long partyId, Long phoneId) {
        // TODO: Implement remove phone logic
        return Mono.empty();
    }

    // Preferred channel operations
    @Override
    public Mono<Void> setPreferredChannel(Long partyId, Object channelData) {
        // TODO: Implement set preferred channel logic
        return Mono.empty();
    }

    // Authorized signatory operations
    @Override
    public Mono<Void> addAuthorizedSignatory(Long partyId, Object signatoryData) {
        // TODO: Implement add authorized signatory logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> removeAuthorizedSignatory(Long partyId) {
        // TODO: Implement remove authorized signatory logic
        return Mono.empty();
    }

    // Status operations
    @Override
    public Mono<Void> markDormant(Long partyId) {
        // TODO: Implement mark dormant logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> reactivate(Long partyId) {
        // TODO: Implement reactivate logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> markDeceased(Long partyId) {
        // TODO: Implement mark deceased logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> requestClosure(Long partyId) {
        // TODO: Implement request closure logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> confirmClosure(Long partyId) {
        // TODO: Implement confirm closure logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> mergeWith(Long partyId, Object mergeData) {
        // TODO: Implement merge with logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> splitFrom(Long partyId, Object splitData) {
        // TODO: Implement split from logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> lockProfile(Long partyId) {
        // TODO: Implement lock profile logic
        return Mono.empty();
    }

    @Override
    public Mono<Void> unlockProfile(Long partyId) {
        // TODO: Implement unlock profile logic
        return Mono.empty();
    }
}