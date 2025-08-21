package com.catalis.domain.people.core.orchestrator;

import com.catalis.domain.people.core.integration.client.CustomersClient;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterCustomerCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterNaturalPersonCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyCommand;
import com.catalis.transactionalengine.annotations.FromStep;
import com.catalis.transactionalengine.annotations.Saga;
import com.catalis.transactionalengine.annotations.SagaStep;
import com.catalis.transactionalengine.core.SagaContext;
import com.catalis.transactionalengine.http.HttpCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Saga(name = "RegisterCustomerSaga")
@Service
public class RegisterCustomerOrchestrator {

    private final CustomersClient customersClient;

    @Autowired
    public RegisterCustomerOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = "registerParty", compensate = "removeParty")
    public Mono<Long> registerParty(RegisterPartyCommand cmd, SagaContext ctx) {
        return customersClient
                .createParty(cmd)
                .mapNotNull(partyDTOResponseEntity ->
                        Objects.requireNonNull(Objects.requireNonNull(partyDTOResponseEntity.getBody()).getPartyId()));
    }

    public Mono<Void> removeParty(Long partyId, SagaContext ctx) {
        return customersClient.deleteParty(partyId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerNaturalPerson", compensate = "removeNaturalPerson", dependsOn = "registerParty")
    public Mono<Long> registerNaturalPerson(RegisterNaturalPersonCommand cmd, SagaContext ctx, @FromStep("registerParty") Long partyId) {
        return customersClient
                .createNaturalPerson(partyId, cmd)
                .mapNotNull(partyDTOResponseEntity ->
                        Objects.requireNonNull(Objects.requireNonNull(partyDTOResponseEntity.getBody()).getNaturalPersonId()));
    }

    public Mono<Void> removeNaturalPerson(Long naturalPersonId, SagaContext ctx) {
        return customersClient.deleteNaturalPerson(naturalPersonId).mapNotNull(HttpEntity::getBody);
    }


}
