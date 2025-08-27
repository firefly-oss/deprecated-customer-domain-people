package com.catalis.domain.people.core.integration.client.impl;

import com.catalis.core.customer.sdk.api.*;
import com.catalis.core.customer.sdk.invoker.ApiClient;
import com.catalis.core.customer.sdk.model.*;
import com.catalis.domain.people.core.integration.client.CustomersClient;
import com.catalis.domain.people.core.integration.mapper.CustomersMapper;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Implementation of CustomersClient that integrates with the customer management system SDK.
 * 
 * This adapter class bridges the domain layer with the external customer management APIs,
 * handling the mapping between domain commands and SDK DTOs. It manages multiple API clients
 * for different customer-related entities and ensures idempotency through UUID generation.
 * 
 * The implementation delegates operations to the appropriate SDK API clients while maintaining
 * reactive programming patterns throughout the integration layer.
 */
@Service
public class CustomersClientImpl implements CustomersClient {

    private final PartiesApi partyApi;
    private final NaturalPersonsApi naturalPersonApi;
    private final LegalEntitiesApi legalPersonApi;
    private final PartyStatusesApi partyStatusApi;
    private final PoliticallyExposedPersonsApi pepApi;
    private final IdentityDocumentsApi identityDocumentApi;
    private final AddressesApi addressApi;
    private final EmailContactsApi emailApi;
    private final PhoneContactsApi phoneApi;
    private final PartyEconomicActivitiesApi partyEconomicActivityApi;
    private final ConsentsApi consentApi;
    private final PartyProvidersApi partyProvidersApi;
    private final PartyRelationshipsApi partyRelationshipsApi;
    private final PartyGroupMembershipsApi partyGroupMembershipsApi;
    private final CustomersMapper customersMapper;

    @Autowired
    public CustomersClientImpl(ApiClient apiClient, CustomersMapper customersMapper) {
        this.partyApi = new PartiesApi(apiClient);
        this.naturalPersonApi = new NaturalPersonsApi(apiClient);
        this.legalPersonApi = new LegalEntitiesApi(apiClient);
        this.partyStatusApi = new PartyStatusesApi(apiClient);
        this.pepApi = new PoliticallyExposedPersonsApi(apiClient);
        this.identityDocumentApi = new IdentityDocumentsApi(apiClient);
        this.addressApi = new AddressesApi(apiClient);
        this.emailApi = new EmailContactsApi(apiClient);
        this.phoneApi = new PhoneContactsApi(apiClient);
        this.partyEconomicActivityApi = new PartyEconomicActivitiesApi(apiClient);
        this.consentApi = new ConsentsApi(apiClient);
        this.partyProvidersApi = new PartyProvidersApi(apiClient);
        this.partyRelationshipsApi = new PartyRelationshipsApi(apiClient);
        this.partyGroupMembershipsApi = new PartyGroupMembershipsApi(apiClient);
        this.customersMapper = customersMapper;
    }

    @Override
    public Mono<ResponseEntity<PartyDTO>> createParty(RegisterPartyCommand registerPartyCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        PartyDTO partyDTO = customersMapper.toPartyDTO(registerPartyCommand);
        return partyApi.createPartyWithHttpInfo(partyDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteParty(Long id) {
        return partyApi.deletePartyWithHttpInfo(id);
    }

    @Override
    public Mono<ResponseEntity<NaturalPersonDTO>> createNaturalPerson(Long partyId, RegisterNaturalPersonCommand registerNaturalPersonCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        NaturalPersonDTO naturalPersonDTO = customersMapper.toNaturalPersonDTO(registerNaturalPersonCommand);
        naturalPersonDTO.setPartyId(partyId);
        return naturalPersonApi.createNaturalPersonWithHttpInfo(partyId, naturalPersonDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteNaturalPerson(Long partyId, Long id) {
        return naturalPersonApi.deleteNaturalPersonWithHttpInfo(partyId, id);
    }

    @Override
    public Mono<ResponseEntity<LegalEntityDTO>> createLegalPerson(Long partyId, RegisterLegalPersonCommand registerLegalPersonCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        LegalEntityDTO legalEntityDTO = customersMapper.toLegalPersonDTO(registerLegalPersonCommand);
        legalEntityDTO.setPartyId(partyId);
        return legalPersonApi.createLegalEntityWithHttpInfo(partyId, legalEntityDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteLegalPerson(Long partyId, Long id) {
        return legalPersonApi.deleteLegalEntityWithHttpInfo(partyId, id);
    }

    @Override
    public Mono<ResponseEntity<PartyStatusDTO>> createPartyStatus(Long partyId, RegisterPartyStatusEntryCommand statusEntryCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        PartyStatusDTO partyStatusDTO = customersMapper.toPartyStatusDTO(statusEntryCommand);
        partyStatusDTO.setPartyId(partyId);
        return partyStatusApi.createPartyStatusWithHttpInfo(partyId, partyStatusDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePartyStatus(Long partyId, Long partyStatusId) {
        return partyStatusApi.deletePartyStatusWithHttpInfo(partyId, partyStatusId);
    }

    @Override
    public Mono<ResponseEntity<PoliticallyExposedPersonDTO>> createPep(Long partyId, RegisterPepCommand pepCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        PoliticallyExposedPersonDTO politicallyExposedPersonDTO = customersMapper.toPepDTO(pepCommand);
        politicallyExposedPersonDTO.setPartyId(partyId);
        return pepApi.createPoliticallyExposedPersonWithHttpInfo(partyId, politicallyExposedPersonDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePep(Long partyId, Long pepId) {
        return pepApi.deletePoliticallyExposedPersonWithHttpInfo(partyId, pepId);
    }

    @Override
    public Mono<ResponseEntity<IdentityDocumentDTO>> createIdentityDocument(Long partyId, RegisterIdentityDocumentCommand identityDocumentCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        IdentityDocumentDTO identityDocumentDTO = customersMapper.toIdentityDocumentDTO(identityDocumentCommand);
        identityDocumentDTO.setPartyId(partyId);
        return identityDocumentApi.createIdentityDocumentWithHttpInfo(partyId, identityDocumentDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteIdentityDocument(Long partyId, Long identityDocumentId) {
        return identityDocumentApi.deleteIdentityDocumentWithHttpInfo(partyId, identityDocumentId);
    }

    @Override
    public Mono<ResponseEntity<AddressDTO>> createAddress(Long partyId, RegisterAddressCommand addressCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        AddressDTO addressDTO = customersMapper.toAddressDTO(addressCommand);
        addressDTO.setPartyId(partyId);
        return addressApi.createAddressWithHttpInfo(partyId, addressDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAddress(Long partyId, Long addressId) {
        return addressApi.deleteAddressWithHttpInfo(partyId, addressId);
    }

    @Override
    public Mono<ResponseEntity<EmailContactDTO>> createEmail(Long partyId, RegisterEmailCommand emailCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        EmailContactDTO emailContactDTO = customersMapper.toEmailDTO(emailCommand);
        emailContactDTO.setPartyId(partyId);
        return emailApi.createEmailContactWithHttpInfo(partyId, emailContactDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteEmail(Long partyId, Long emailId) {
        return emailApi.deleteEmailContactWithHttpInfo(partyId, emailId);
    }

    @Override
    public Mono<ResponseEntity<PhoneContactDTO>> createPhone(Long partyId, RegisterPhoneCommand phoneCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        PhoneContactDTO phoneContactDTO = customersMapper.toPhoneDTO(phoneCommand);
        phoneContactDTO.setPartyId(partyId);
        return phoneApi.createPhoneContactWithHttpInfo(partyId, phoneContactDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePhone(Long partyId, Long phoneId) {
        return phoneApi.deletePhoneContactWithHttpInfo(partyId, phoneId);
    }

    @Override
    public Mono<ResponseEntity<PartyEconomicActivityDTO>> createPartyEconomicActivity(Long partyId, RegisterEconomicActivityLinkCommand economicActivityLinkCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        PartyEconomicActivityDTO partyEconomicActivityDTO = customersMapper.toPartyEconomicActivityDTO(economicActivityLinkCommand);
        partyEconomicActivityDTO.setPartyId(partyId);
        return partyEconomicActivityApi.createPartyEconomicActivityWithHttpInfo(partyId,
                partyEconomicActivityDTO,
                xIdempotencyKey
        );
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePartyEconomicActivity(Long partyId, Long partyEconomicActivityId) {
        return partyEconomicActivityApi.deletePartyEconomicActivityWithHttpInfo(partyId, partyEconomicActivityId);
    }

    @Override
    public Mono<ResponseEntity<ConsentDTO>> createConsent(Long partyId, RegisterConsentCommand consentCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        ConsentDTO consentDTO = customersMapper.toConsentDTO(consentCommand);
        consentDTO.setPartyId(partyId);
        return consentApi.createConsentWithHttpInfo(
                partyId,
                consentDTO,
                xIdempotencyKey
        );
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteConsent(Long partyId, Long consentId) {
        return consentApi.deleteConsentWithHttpInfo(partyId, consentId);
    }

    @Override
    public Mono<ResponseEntity<PartyProviderDTO>> createPartyProvider(Long partyId, RegisterPartyProviderCommand partyProviderCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        PartyProviderDTO partyProviderDTO = customersMapper.toPartyProviderDTO(partyProviderCommand);
        partyProviderDTO.setPartyId(partyId);
        return partyProvidersApi.createPartyProviderWithHttpInfo(partyId, partyProviderDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePartyProvider(Long partyId, Long partyProviderId) {
        return partyProvidersApi.deletePartyProviderWithHttpInfo(partyId, partyProviderId);
    }

    @Override
    public Mono<ResponseEntity<PartyRelationshipDTO>> createPartyRelationshipWithHttpInfo(Long partyId, RegisterPartyRelationshipCommand partyRelationshipCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        PartyRelationshipDTO partyRelationshipDTO = customersMapper.toPartyRelationshipDTO(partyRelationshipCommand);
        partyRelationshipDTO.setFromPartyId(partyId);
        return partyRelationshipsApi.createPartyRelationshipWithHttpInfo(partyId, partyRelationshipDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePartyRelationshipWithHttpInfo(Long partyId, Long partyRelationshipId) {
        return partyRelationshipsApi.deletePartyRelationshipWithHttpInfo(partyId, partyRelationshipId);
    }

    @Override
    public Mono<ResponseEntity<PartyGroupMembershipDTO>> createPartyGroupMembershipWithHttpInfo(Long partyId, RegisterPartyGroupMembershipCommand partyGroupMembershipCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        PartyGroupMembershipDTO partyGroupMembershipDTO = customersMapper.toPartyGroupMembershipDTO(partyGroupMembershipCommand);
        partyGroupMembershipDTO.setPartyId(partyId);
        return partyGroupMembershipsApi.createPartyGroupMembershipWithHttpInfo(partyId, partyGroupMembershipDTO, xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePartyGroupMembershipWithHttpInfo(Long partyId, Long partyGroupMembershipId) {
        return partyGroupMembershipsApi.deletePartyGroupMembershipWithHttpInfo(partyId, partyGroupMembershipId);
    }
}
