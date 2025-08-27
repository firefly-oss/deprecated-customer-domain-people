package com.catalis.domain.people.core.integration.client;

import com.catalis.core.customer.sdk.model.*;
import com.catalis.domain.people.interfaces.dto.commands.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Client interface for integrating with the customer management system.
 * 
 * This interface provides reactive operations for managing customer-related entities
 * including parties, persons, addresses, contacts, and relationships. All operations
 * return Mono types for non-blocking reactive programming support.
 */
public interface CustomersClient {

    Mono<ResponseEntity<PartyDTO>> createParty(RegisterPartyCommand registerPartyCommand);

    Mono<ResponseEntity<Void>> deleteParty(Long id);

    Mono<ResponseEntity<NaturalPersonDTO>> createNaturalPerson(Long partyId, RegisterNaturalPersonCommand naturalPersonCommand);

    Mono<ResponseEntity<Void>> deleteNaturalPerson(Long partyId, Long id);

    Mono<ResponseEntity<LegalEntityDTO>> createLegalPerson(Long partyId, RegisterLegalPersonCommand legalPersonCommand);

    Mono<ResponseEntity<PartyStatusDTO>> createPartyStatus(Long partyId, RegisterPartyStatusEntryCommand statusEntryCommand);

    Mono<ResponseEntity<Void>> deletePartyStatus(Long partyId, Long partyStatusId);

    Mono<ResponseEntity<PoliticallyExposedPersonDTO>> createPep(Long partyId, RegisterPepCommand pepCommand);

    Mono<ResponseEntity<Void>> deleteLegalPerson(Long partyId, Long id);

    Mono<ResponseEntity<Void>> deletePep(Long partyId, Long pepId);

    Mono<ResponseEntity<IdentityDocumentDTO>> createIdentityDocument(Long partyId, RegisterIdentityDocumentCommand identityDocumentCommand);

    Mono<ResponseEntity<Void>> deleteIdentityDocument(Long partyId, Long identityDocumentId);

    // Address operations
    Mono<ResponseEntity<AddressDTO>> createAddress(Long partyId, RegisterAddressCommand addressCommand);

    Mono<ResponseEntity<Void>> deleteAddress(Long partyId, Long addressId);

    // Email operations
    Mono<ResponseEntity<EmailContactDTO>> createEmail(Long partyId, RegisterEmailCommand emailCommand);

    Mono<ResponseEntity<Void>> deleteEmail(Long partyId, Long emailId);

    // Phone operations
    Mono<ResponseEntity<PhoneContactDTO>> createPhone(Long partyId, RegisterPhoneCommand phoneCommand);

    Mono<ResponseEntity<Void>> deletePhone(Long partyId, Long phoneId);

    // Economic Activity operations
    Mono<ResponseEntity<PartyEconomicActivityDTO>> createPartyEconomicActivity(Long partyId, RegisterEconomicActivityLinkCommand economicActivityLinkCommand);

    Mono<ResponseEntity<Void>> deletePartyEconomicActivity(Long partyId, Long partyEconomicActivityId);

    // Consent operations
    Mono<ResponseEntity<ConsentDTO>> createConsent(Long partyId, RegisterConsentCommand consentCommand);

    Mono<ResponseEntity<Void>> deleteConsent(Long partyId, Long consentId);

    // Party Provider operations
    Mono<ResponseEntity<PartyProviderDTO>> createPartyProvider(Long partyId, RegisterPartyProviderCommand partyProviderCommand);

    Mono<ResponseEntity<Void>> deletePartyProvider(Long partyId, Long partyProviderId);

    // Party Relationship operations
    Mono<ResponseEntity<PartyRelationshipDTO>> createPartyRelationshipWithHttpInfo(Long partyId, RegisterPartyRelationshipCommand partyRelationshipCommand);

    Mono<ResponseEntity<Void>> deletePartyRelationshipWithHttpInfo(Long partyId, Long partyRelationshipId);

    // Party Group Membership operations
    Mono<ResponseEntity<PartyGroupMembershipDTO>> createPartyGroupMembershipWithHttpInfo(Long partyId, RegisterPartyGroupMembershipCommand partyGroupMembershipCommand);

    Mono<ResponseEntity<Void>> deletePartyGroupMembershipWithHttpInfo(Long partyId, Long partyGroupMembershipId);
}