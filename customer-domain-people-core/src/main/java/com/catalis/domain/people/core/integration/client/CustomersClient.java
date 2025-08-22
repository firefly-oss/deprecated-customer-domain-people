package com.catalis.domain.people.core.integration.client;

import com.catalis.common.customer.sdk.model.*;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;


public interface CustomersClient {


    /**
     * Creates a new party based on the provided registration command.
     *
     * @param registerPartyCommand the command containing the details required to create a party,
     *                             including party type, preferred language, and record source
     * @return a {@code Mono<ResponseEntity<PartyDTO>>} containing the details of the newly created party
     */
    Mono<ResponseEntity<PartyDTO>> createParty(RegisterPartyCommand registerPartyCommand);


    Mono<ResponseEntity<Void>> deleteParty(Long id);

    Mono<ResponseEntity<NaturalPersonDTO>> createNaturalPerson(Long partyId, RegisterNaturalPersonCommand naturalPersonCommand);

    Mono<ResponseEntity<Void>> deleteNaturalPerson(Long id);

    Mono<ResponseEntity<LegalPersonDTO>> createLegalPerson(Long partyId, RegisterLegalPersonCommand legalPersonCommand);

    Mono<ResponseEntity<Void>> deleteLegalPerson(Long id);

    Mono<ResponseEntity<PartyStatusDTO>> createPartyStatus(Long partyId, RegisterPartyStatusEntryCommand statusEntryCommand);

    Mono<ResponseEntity<Void>> deletePartyStatus(Long partyId, Long partyStatusId);

    Mono<ResponseEntity<PepDTO>> createPep(Long partyId, RegisterPepCommand pepCommand);

    Mono<ResponseEntity<Void>> deletePep(Long partyId, Long pepId);

    Mono<ResponseEntity<IdentityDocumentDTO>> createIdentityDocument(Long partyId, RegisterIdentityDocumentCommand identityDocumentCommand);

    Mono<ResponseEntity<Void>> deleteIdentityDocument(Long partyId, Long identityDocumentId);

    // Address operations
    Mono<ResponseEntity<AddressDTO>> createAddress(Long partyId, RegisterAddressCommand addressCommand);

    Mono<ResponseEntity<Void>> deleteAddress(Long partyId, Long addressId);

    // Email operations
    Mono<ResponseEntity<EmailDTO>> createEmail(Long partyId, RegisterEmailCommand emailCommand);

    Mono<ResponseEntity<Void>> deleteEmail(Long partyId, Long emailId);
}