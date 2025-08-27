package com.catalis.domain.people.core.service;

import com.catalis.domain.people.interfaces.dto.commands.RegisterAddressCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterCustomerCommand;
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
    Mono<Void> updateName(Long partyId, String newName);

    // Address operations
    Mono<SagaResult> addAddress(Long partyId, RegisterAddressCommand addressCommand);
    Mono<Void> updateAddress(Long partyId, Long addressId, Object addressData);
    Mono<Void> removeAddress(Long partyId, Long addressId);

    // Email operations
    Mono<Void> addEmail(Long partyId, Object emailData);
    Mono<Void> removeEmail(Long partyId, Long emailId);

    // Phone operations
    Mono<Void> addPhone(Long partyId, Object phoneData);
    Mono<Void> removePhone(Long partyId, Long phoneId);

    // Preferred channel operations
    Mono<Void> setPreferredChannel(Long partyId, Object channelData);

    // Authorized signatory operations
    Mono<Void> addAuthorizedSignatory(Long partyId, Object signatoryData);
    Mono<Void> removeAuthorizedSignatory(Long partyId);

    // Status operations
    Mono<Void> markDormant(Long partyId);
    Mono<Void> reactivate(Long partyId);
    Mono<Void> markDeceased(Long partyId);
    Mono<Void> requestClosure(Long partyId);
    Mono<Void> confirmClosure(Long partyId);
    Mono<Void> mergeWith(Long partyId, Object mergeData);
    Mono<Void> splitFrom(Long partyId, Object splitData);
    Mono<Void> lockProfile(Long partyId);
    Mono<Void> unlockProfile(Long partyId);
}