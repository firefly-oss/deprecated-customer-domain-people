package com.catalis.domain.people.core.integration.client.impl;

import com.catalis.common.customer.sdk.api.NaturalPersonApi;
import com.catalis.common.customer.sdk.api.PartyApi;
import com.catalis.common.customer.sdk.invoker.ApiClient;
import com.catalis.common.customer.sdk.model.NaturalPersonDTO;
import com.catalis.common.customer.sdk.model.PartyDTO;
import com.catalis.domain.people.core.integration.client.CustomersClient;
import com.catalis.domain.people.core.integration.mapper.CustomersMapper;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterNaturalPersonCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Service
public class CustomersClientImpl implements CustomersClient {

    private final PartyApi partyApi;
    private final NaturalPersonApi naturalPersonApi;
    private final CustomersMapper customersMapper;

    @Autowired
    public CustomersClientImpl(ApiClient apiClient, CustomersMapper customersMapper) {
        this.partyApi = new PartyApi(apiClient);
        this.naturalPersonApi = new NaturalPersonApi(apiClient);
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

}
