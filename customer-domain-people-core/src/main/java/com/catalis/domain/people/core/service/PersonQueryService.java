package com.catalis.domain.people.core.service;

import com.catalis.domain.people.interfaces.dto.query.PersonView;
import reactor.core.publisher.Mono;

/**
 * Query service (CQ) for people
 */
public interface PersonQueryService {
    
    /**
     * Retrieves a person by their customer ID.
     *
     * @param customerId the ID of the customer to retrieve
     * @return a Mono containing the PersonView if found, empty otherwise
     */
    Mono<PersonView> getCustomerById(Long customerId);
}