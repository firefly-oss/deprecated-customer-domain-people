package com.firefly.domain.people.core.service.impl;

import com.firefly.domain.people.core.orchestrator.address.AddAddressOrchestrator;
import com.firefly.domain.people.core.orchestrator.address.RemoveAddressOrchestrator;
import com.firefly.domain.people.core.orchestrator.address.UpdateAddressOrchestrator;
import com.firefly.domain.people.core.orchestrator.channel.SetPreferredChannelOrchestrator;
import com.firefly.domain.people.core.orchestrator.customer.RegisterCustomerOrchestrator;
import com.firefly.domain.people.core.orchestrator.customer.UpdateNameOrchestrator;
import com.firefly.domain.people.core.orchestrator.email.AddEmailOrchestrator;
import com.firefly.domain.people.core.orchestrator.email.RemoveEmailOrchestrator;
import com.firefly.domain.people.core.orchestrator.phone.AddPhoneOrchestrator;
import com.firefly.domain.people.core.orchestrator.phone.RemovePhoneOrchestrator;
import com.firefly.domain.people.core.orchestrator.status.UpdateStatusOrchestrator;
import com.firefly.domain.people.core.service.CommandService;
import com.firefly.domain.people.interfaces.dto.commands.*;
import com.firefly.transactionalengine.core.SagaResult;
import com.firefly.transactionalengine.engine.ExpandEach;
import com.firefly.transactionalengine.engine.SagaEngine;
import com.firefly.transactionalengine.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

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
    public Mono<Void> setPreferredChannel(Long partyId, PreferredChannelCommand channelData) {
        StepInputs inputs = StepInputs.builder()
                .forStep(SetPreferredChannelOrchestrator::updateChannel, channelData.withPartyId(partyId))
                .build();

        return engine.execute(SetPreferredChannelOrchestrator.class, inputs)
                .then();
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
    public Mono<SagaResult> markDormant(Long partyId) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateStatusOrchestrator::updateStatus,
                        new RegisterPartyStatusEntryCommand(partyId,
                                "INACTIVE",
                                "User has been marked as dormant due to inactivity",
                                LocalDateTime.now(),
                                null))
                .build();

        return engine.execute(UpdateStatusOrchestrator.class, inputs);
    }

    @Override
    public Mono<SagaResult> reactivate(Long partyId) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateStatusOrchestrator::updateStatus,
                        new RegisterPartyStatusEntryCommand(partyId,
                                "ACTIVE",
                                "User account has been reactivated and is now fully usable.",
                                LocalDateTime.now(),
                                null))
                .build();

        return engine.execute(UpdateStatusOrchestrator.class, inputs);
    }

    @Override
    public Mono<SagaResult> markDeceased(Long partyId) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateStatusOrchestrator::updateStatus,
                        new RegisterPartyStatusEntryCommand(partyId,
                                "CLOSED",
                                "User account is permanently closed because the user is deceased.",
                                LocalDateTime.now(),
                                null))
                .build();

        return engine.execute(UpdateStatusOrchestrator.class, inputs);
    }

    @Override
    public Mono<SagaResult> requestClosure(Long partyId) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateStatusOrchestrator::updateStatus,
                        new RegisterPartyStatusEntryCommand(partyId,
                                "PENDING",
                                "A closure request has been submitted but is not yet confirmed.",
                                LocalDateTime.now(),
                                null))
                .build();

        return engine.execute(UpdateStatusOrchestrator.class, inputs);
    }

    @Override
    public Mono<SagaResult> confirmClosure(Long partyId) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateStatusOrchestrator::updateStatus,
                        new RegisterPartyStatusEntryCommand(partyId,
                                "CLOSED",
                                "Closure has been confirmed and the account is permanently closed.",
                                LocalDateTime.now(),
                                null))
                .build();

        return engine.execute(UpdateStatusOrchestrator.class, inputs);
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
    public Mono<SagaResult> lockProfile(Long partyId) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateStatusOrchestrator::updateStatus,
                        new RegisterPartyStatusEntryCommand(partyId,
                                "SUSPENDED",
                                "Profile is temporarily locked, restricting access and activity.",
                                LocalDateTime.now(),
                                null))
                .build();

        return engine.execute(UpdateStatusOrchestrator.class, inputs);
    }

    @Override
    public Mono<SagaResult> unlockProfile(Long partyId) {
        StepInputs inputs = StepInputs.builder()
                .forStep(UpdateStatusOrchestrator::updateStatus,
                        new RegisterPartyStatusEntryCommand(partyId,
                                "ACTIVE",
                                "Lock has been removed; user profile is restored to active status.",
                                LocalDateTime.now(),
                                null))
                .build();

        return engine.execute(UpdateStatusOrchestrator.class, inputs);
    }
}