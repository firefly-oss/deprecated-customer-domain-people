package com.catalis.domain.people.core.integration.client;

import com.catalis.common.customer.sdk.model.LegalPersonDTO;
import com.catalis.common.customer.sdk.model.NaturalPersonDTO;
import com.catalis.common.customer.sdk.model.PartyDTO;
import com.catalis.common.customer.sdk.model.PartyStatusDTO;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterLegalPersonCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterNaturalPersonCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyStatusEntryCommand;
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

}