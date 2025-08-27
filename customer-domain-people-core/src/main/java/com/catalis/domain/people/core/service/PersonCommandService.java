package com.catalis.domain.people.core.service;

import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterCustomerCommand;
import com.catalis.transactionalengine.core.SagaResult;
import reactor.core.publisher.Mono;

/**
 * Command service interface for person-related operations.
 * Handles customer registration processes using saga orchestration patterns.
 */
public interface PersonCommandService {
    
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
     * @param customerId the ID of the customer whose name will be updated
     * @param newName the new name for the customer
     * @return a Mono signaling completion
     */
    Mono<Void> updateName(Long customerId, String newName);

    // Address operations
    Mono<Void> addAddress(Long customerId, Object addressData);
    Mono<Void> updateAddress(Long customerId, Long addressId, Object addressData);
    Mono<Void> removeAddress(Long customerId, Long addressId);

    // Email operations
    Mono<Void> addEmail(Long customerId, Object emailData);
    Mono<Void> removeEmail(Long customerId, Long emailId);

    // Phone operations
    Mono<Void> addPhone(Long customerId, Object phoneData);
    Mono<Void> removePhone(Long customerId, Long phoneId);

    // Preferred channel operations
    Mono<Void> setPreferredChannel(Long customerId, Object channelData);

    // Authorized signatory operations
    Mono<Void> addAuthorizedSignatory(Long customerId, Object signatoryData);
    Mono<Void> removeAuthorizedSignatory(Long customerId, Long partyId);

    // Status operations
    Mono<Void> markDormant(Long customerId);
    Mono<Void> reactivate(Long customerId);
    Mono<Void> markDeceased(Long customerId);
    Mono<Void> requestClosure(Long customerId);
    Mono<Void> confirmClosure(Long customerId);
    Mono<Void> mergeWith(Long customerId, Object mergeData);
    Mono<Void> splitFrom(Long customerId, Object splitData);
    Mono<Void> lockProfile(Long customerId);
    Mono<Void> unlockProfile(Long customerId);
}