package com.catalis.domain.people.core.service.impl;

import com.catalis.domain.people.core.orchestrator.address.AddAddressOrchestrator;
import com.catalis.domain.people.core.orchestrator.address.RemoveAddressOrchestrator;
import com.catalis.domain.people.core.orchestrator.address.UpdateAddressOrchestrator;
import com.catalis.domain.people.core.orchestrator.customer.RegisterCustomerOrchestrator;
import com.catalis.domain.people.core.orchestrator.customer.UpdateNameOrchestrator;
import com.catalis.domain.people.core.orchestrator.email.AddEmailOrchestrator;
import com.catalis.domain.people.core.orchestrator.email.RemoveEmailOrchestrator;
import com.catalis.domain.people.core.orchestrator.phone.AddPhoneOrchestrator;
import com.catalis.domain.people.core.orchestrator.phone.RemovePhoneOrchestrator;
import com.catalis.domain.people.core.service.CommandService;
import com.catalis.domain.people.interfaces.dto.commands.*;
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
    public Mono<SagaResult> updateName(Long partyId, String newName) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateNameOrchestrator::retrievePartyId, new UpdateNameCommand(partyId, newName))
                .forStep(UpdateNameOrchestrator::retrieveCustomer, new UpdateNameCommand(partyId, newName))
                .forStep(UpdateNameOrchestrator::updateName, new UpdateNameCommand(partyId, newName))
                .build();

        return engine.execute(UpdateNameOrchestrator.class, inputs);

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
    public Mono<Void> updateAddress(Long partyId, Long addressId, RegisterAddressCommand addressData) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateAddressOrchestrator::updateAddress, addressData.withAddressId(addressId).withPartyId(partyId))
                .build();

        return engine.execute(UpdateAddressOrchestrator.class, inputs)
                .then();
    }

    @Override
    public Mono<SagaResult> removeAddress(Long partyId, Long addressId) {
        StepInputs inputs = StepInputs.builder()
                .forStep(RemoveAddressOrchestrator::removeAddress, new RemoveAddressCommand(partyId, addressId))
                .build();

        return engine.execute(RemoveAddressOrchestrator.class, inputs);
    }

    // Email operations
    @Override
    public Mono<SagaResult> addEmail(Long partyId, RegisterEmailCommand emailCommand) {
        StepInputs inputs = StepInputs.builder()
                .forStep(AddEmailOrchestrator::registerEmail, emailCommand.withPartyId(partyId))
                .build();

        return engine.execute(AddEmailOrchestrator.class, inputs);
    }

    @Override
    public Mono<SagaResult> removeEmail(Long partyId, Long emailId) {
        StepInputs inputs = StepInputs.builder()
                .forStep(RemoveEmailOrchestrator::removeEmail, new RemoveEmailCommand(partyId, emailId))
                .build();

        return engine.execute(RemoveEmailOrchestrator.class, inputs);
    }

    // Phone operations
    @Override
    public Mono<SagaResult> addPhone(Long partyId, RegisterPhoneCommand phoneCommand) {
        StepInputs inputs = StepInputs.builder()
                .forStep(AddPhoneOrchestrator::registerPhone, phoneCommand.withPartyId(partyId))
                .build();

        return engine.execute(AddPhoneOrchestrator.class, inputs);
    }

    @Override
    public Mono<SagaResult> removePhone(Long partyId, Long phoneId) {
        StepInputs inputs = StepInputs.builder()
                .forStep(RemovePhoneOrchestrator::removePhone, new RemovePhoneCommand(partyId, phoneId))
                .build();

        return engine.execute(RemovePhoneOrchestrator.class, inputs);
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