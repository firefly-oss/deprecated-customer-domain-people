package com.catalis.domain.people.core.orchestrator;

import com.catalis.domain.people.core.integration.client.CustomersClient;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterCustomerCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterLegalPersonCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterNaturalPersonCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyStatusEntryCommand;
import com.catalis.transactionalengine.annotations.FromStep;
import com.catalis.transactionalengine.annotations.Saga;
import com.catalis.transactionalengine.annotations.SagaStep;
import com.catalis.transactionalengine.core.SagaContext;
import com.catalis.transactionalengine.http.HttpCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Saga(name = "RegisterCustomerSaga")
@Service
public class RegisterCustomerOrchestrator {

    public static final String X_CUSTOMER_TYPE = "X-Customer-Type";
    public static final String LEGAL_PERSON = "LEGAL_PERSON";
    private final CustomersClient customersClient;

    @Autowired
    public RegisterCustomerOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = "registerParty", compensate = "removeParty")
    public Mono<Long> registerParty(RegisterPartyCommand cmd, SagaContext ctx) {
        ctx.variables().put(X_CUSTOMER_TYPE, cmd.partyType());
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
        if(!ctx.variables().get(X_CUSTOMER_TYPE).equals("NATURAL_PERSON")){
            return Mono.empty();
        }
        return customersClient
                .createNaturalPerson(partyId, cmd)
                .mapNotNull(partyDTOResponseEntity ->
                        Objects.requireNonNull(Objects.requireNonNull(partyDTOResponseEntity.getBody()).getNaturalPersonId()));
    }

    public Mono<Void> removeNaturalPerson(Long naturalPersonId, SagaContext ctx) {
        if(!ctx.variables().get(X_CUSTOMER_TYPE).equals("NATURAL_PERSON")){
            return Mono.empty();
        }
        return customersClient.deleteNaturalPerson(naturalPersonId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerLegalPerson", compensate = "removeLegalPerson", dependsOn = "registerParty")
    public Mono<Long> registerLegalPerson(RegisterLegalPersonCommand cmd, SagaContext ctx, @FromStep("registerParty") Long partyId) {
        if(!ctx.variables().get(X_CUSTOMER_TYPE).equals("LEGAL_PERSON")){
            return Mono.empty();
        }
        return customersClient
                .createLegalPerson(partyId, cmd)
                .mapNotNull(legalPersonDTOResponseEntity ->
                        Objects.requireNonNull(Objects.requireNonNull(legalPersonDTOResponseEntity.getBody()).getLegalPersonId()));
    }

    public Mono<Void> removeLegalPerson(Long legalPersonId, SagaContext ctx) {
        if(!ctx.variables().get(X_CUSTOMER_TYPE).equals(LEGAL_PERSON)){
            return Mono.empty();
        }
        return customersClient.deleteLegalPerson(legalPersonId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerStatusEntry", compensate = "removeStatusEntry", dependsOn = "registerParty")
    public Mono<Long> registerStatusEntry(RegisterPartyStatusEntryCommand cmd, SagaContext ctx, @FromStep("registerParty") Long partyId) {
        return customersClient
                        .createPartyStatus(partyId, cmd)
                        .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getPartyStatusId()));
    }

    public Mono<Void> removeStatusEntry(List<Long> statusIds, SagaContext ctx, @FromStep("registerParty") Long partyId) {
        if (statusIds == null || statusIds.isEmpty()) {
            return Mono.empty();
        }
        return Flux.fromIterable(statusIds)
                .concatMap(id -> customersClient.deletePartyStatus(partyId, id).mapNotNull(HttpEntity::getBody))
                .then();
    }

}
