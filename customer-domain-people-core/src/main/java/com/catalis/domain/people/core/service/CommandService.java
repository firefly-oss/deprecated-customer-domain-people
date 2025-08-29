package com.catalis.domain.people.core.service;

import com.catalis.domain.people.interfaces.dto.commands.*;
import com.catalis.transactionalengine.core.SagaResult;
import reactor.core.publisher.Mono;

/**
 * Command service interface for person-related operations.
 * Handles customer registration processes using saga orchestration patterns.
 */
public interface CommandService {
    
    /**
     * Registers a new customer using the provided registration command.
     * This operation is orchestrated as a saga to ensure data consistency across multiple steps.
     *
     * @param command the registration command containing all customer information
     * @return a Mono containing the saga execution result
     */
    Mono<SagaResult> register(RegisterCustomerCommand command);
    
    /**
     * Updates the name of an existing customer.
     *
     * @param partyId the ID of the customer whose name will be updated
     * @param newName the new name for the customer
     * @return a Mono signaling completion
     */
    Mono<SagaResult> updateName(Long partyId, String newName);

    // Address operations
    Mono<SagaResult> addAddress(Long partyId, RegisterAddressCommand addressCommand);
    Mono<Void> updateAddress(Long partyId, Long addressId, RegisterAddressCommand addressData);
    Mono<SagaResult> removeAddress(Long partyId, Long addressId);

    // Email operations
    Mono<SagaResult> addEmail(Long partyId, RegisterEmailCommand emailCommand);
    Mono<SagaResult> removeEmail(Long partyId, Long emailId);

    // Phone operations
    Mono<SagaResult> addPhone(Long partyId, RegisterPhoneCommand phoneCommand);
    Mono<SagaResult> removePhone(Long partyId, Long phoneId);

    // Preferred channel operations
    Mono<Void> setPreferredChannel(Long partyId, PreferredChannelCommand channelData);

    // Authorized signatory operations
    Mono<Void> addAuthorizedSignatory(Long partyId, Object signatoryData);
    Mono<Void> removeAuthorizedSignatory(Long partyId);

    // Status operations
    Mono<SagaResult> markDormant(Long partyId);
    Mono<SagaResult> reactivate(Long partyId);
    Mono<SagaResult> markDeceased(Long partyId);
    Mono<SagaResult> requestClosure(Long partyId);
    Mono<SagaResult> confirmClosure(Long partyId);
    Mono<Void> mergeWith(Long partyId, Object mergeData);
    Mono<Void> splitFrom(Long partyId, Object splitData);
    Mono<SagaResult> lockProfile(Long partyId);
    Mono<SagaResult> unlockProfile(Long partyId);
}