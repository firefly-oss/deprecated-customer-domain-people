package com.catalis.domain.people.core.integration.client.impl;

import com.catalis.common.customer.sdk.api.*;
import com.catalis.common.customer.sdk.invoker.ApiClient;
import com.catalis.common.customer.sdk.model.*;
import com.catalis.domain.people.core.integration.client.CustomersClient;
import com.catalis.domain.people.core.integration.mapper.CustomersMapper;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.*;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Service
public class CustomersClientImpl implements CustomersClient {

    private final PartyApi partyApi;
    private final NaturalPersonApi naturalPersonApi;
    private final LegalPersonApi legalPersonApi;
    private final PartyStatusApi partyStatusApi;
    private final PepApi pepApi;
    private final IdentityDocumentApi identityDocumentApi;
    private final AddressApi addressApi;
    private final EmailApi emailApi;
    private final CustomersMapper customersMapper;

    @Autowired
    public CustomersClientImpl(ApiClient apiClient, CustomersMapper customersMapper) {
        this.partyApi = new PartyApi(apiClient);
        this.naturalPersonApi = new NaturalPersonApi(apiClient);
        this.legalPersonApi = new LegalPersonApi(apiClient);
        this.partyStatusApi = new PartyStatusApi(apiClient);
        this.pepApi = new PepApi(apiClient);
        this.identityDocumentApi = new IdentityDocumentApi(apiClient);
        this.addressApi = new AddressApi(apiClient);
        this.emailApi = new EmailApi(apiClient);
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
        return naturalPersonApi.createNaturalPersonWithHttpInfo(partyId,
                customersMapper.toNaturalPersonDTO(registerNaturalPersonCommand), xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteNaturalPerson(Long id) {
        return naturalPersonApi.deleteNaturalPersonWithHttpInfo(id);
    }

    @Override
    public Mono<ResponseEntity<LegalPersonDTO>> createLegalPerson(Long partyId, RegisterLegalPersonCommand registerLegalPersonCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        return legalPersonApi.createLegalPersonWithHttpInfo(partyId,
                customersMapper.toLegalPersonDTO(registerLegalPersonCommand), xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteLegalPerson(Long id) {
        return legalPersonApi.deleteLegalPersonWithHttpInfo(id);
    }

    @Override
    public Mono<ResponseEntity<PartyStatusDTO>> createPartyStatus(Long partyId, RegisterPartyStatusEntryCommand statusEntryCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        return partyStatusApi.createPartyStatusWithHttpInfo(partyId, customersMapper.toPartyStatusDTO(statusEntryCommand), xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePartyStatus(Long partyId, Long partyStatusId) {
        return partyStatusApi.deletePartyStatusWithHttpInfo(partyId, partyStatusId);
    }

    @Override
    public Mono<ResponseEntity<PepDTO>> createPep(Long partyId, RegisterPepCommand pepCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        return pepApi.createPepWithHttpInfo(partyId, customersMapper.toPepDTO(pepCommand), xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deletePep(Long partyId, Long pepId) {
        return pepApi.deletePepWithHttpInfo(partyId, pepId);
    }

    @Override
    public Mono<ResponseEntity<IdentityDocumentDTO>> createIdentityDocument(Long partyId, RegisterIdentityDocumentCommand identityDocumentCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        return identityDocumentApi.createIdentityDocumentWithHttpInfo(partyId,
                customersMapper.toIdentityDocumentDTO(identityDocumentCommand), xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteIdentityDocument(Long partyId, Long identityDocumentId) {
        return identityDocumentApi.deleteIdentityDocumentWithHttpInfo(partyId, identityDocumentId);
    }

    @Override
    public Mono<ResponseEntity<AddressDTO>> createAddress(Long partyId, RegisterAddressCommand addressCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        return addressApi.createAddressWithHttpInfo(partyId,
                customersMapper.toAddressDTO(addressCommand), xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAddress(Long partyId, Long addressId) {
        return addressApi.deleteAddressWithHttpInfo(partyId, addressId);
    }

    @Override
    public Mono<ResponseEntity<EmailDTO>> createEmail(Long partyId, RegisterEmailCommand emailCommand) {
        String xIdempotencyKey = UUID.randomUUID().toString();
        return emailApi.createEmailWithHttpInfo(partyId,
                customersMapper.toEmailDTO(emailCommand), xIdempotencyKey);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteEmail(Long partyId, Long emailId) {
        return emailApi.deleteEmailWithHttpInfo(partyId, emailId);
    }

}
