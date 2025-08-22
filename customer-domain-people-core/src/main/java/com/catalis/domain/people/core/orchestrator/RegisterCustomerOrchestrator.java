package com.catalis.domain.people.core.orchestrator;

import com.catalis.domain.people.core.integration.client.CustomersClient;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterCustomerCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterLegalPersonCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterNaturalPersonCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyStatusEntryCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPepCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterIdentityDocumentCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterAddressCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterEmailCommand;
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
                        Objects.requireNonNull(Objects.requireNonNull(partyDTOResponseEntity.getBody()).getPartyId()))
                .doOnNext(partyId -> ctx.variables().put("partyId", partyId));
    }

    public Mono<Void> removeParty(Long partyId) {
        return customersClient.deleteParty(partyId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerNaturalPerson", compensate = "removeNaturalPerson", dependsOn = "registerParty")
    public Mono<Long> registerNaturalPerson(RegisterNaturalPersonCommand cmd, SagaContext ctx) {
        if(!ctx.variables().get(X_CUSTOMER_TYPE).equals("NATURAL_PERSON")){
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get("partyId");
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
    public Mono<Long> registerLegalPerson(RegisterLegalPersonCommand cmd, SagaContext ctx) {
        if(!ctx.variables().get(X_CUSTOMER_TYPE).equals("LEGAL_PERSON")){
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get("partyId");
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

    @SagaStep(id = "registerStatusEntry", compensate = "removeStatusEntry", dependsOn = {"registerNaturalPerson", "registerLegalPerson"})
    public Mono<Long> registerStatusEntry(RegisterPartyStatusEntryCommand cmd, SagaContext ctx) {
        Long partyId = (Long) ctx.variables().get("partyId");
        return customersClient
                        .createPartyStatus(partyId, cmd)
                        .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getPartyStatusId()));
    }

    public Mono<Void> removeStatusEntry(Long id, SagaContext ctx) {
        Long partyId = (Long) ctx.variables().get("partyId");
        return customersClient.deletePartyStatus(partyId, id).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerPep", compensate = "removePep", dependsOn = "registerStatusEntry")
    public Mono<Long> registerPep(RegisterPepCommand cmd, SagaContext ctx) {
        if (cmd == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get("partyId");
        return customersClient
                .createPep(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getPepId()));
    }

    public Mono<Void> removePep(Long pepId, SagaContext ctx) {
        if (pepId == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get("partyId");
        return customersClient.deletePep(partyId, pepId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerIdentityDocument", compensate = "removeIdentityDocument", dependsOn = "registerPep")
    public Mono<Long> registerIdentityDocument(RegisterIdentityDocumentCommand cmd, SagaContext ctx) {
        if (cmd == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get("partyId");
        return customersClient
                .createIdentityDocument(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getIdentityDocumentId()));
    }

    public Mono<Void> removeIdentityDocument(Long identityDocumentId, SagaContext ctx) {
        if (identityDocumentId == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get("partyId");
        return customersClient.deleteIdentityDocument(partyId, identityDocumentId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerAddress", compensate = "removeAddress", dependsOn = "registerIdentityDocument")
    public Mono<Long> registerAddress(RegisterAddressCommand cmd, SagaContext ctx) {
        if (cmd == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get("partyId");
        return customersClient
                .createAddress(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getAddressId()));
    }

    public Mono<Void> removeAddress(Long addressId, SagaContext ctx) {
        if (addressId == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get("partyId");
        return customersClient.deleteAddress(partyId, addressId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerEmail", compensate = "removeEmail", dependsOn = "registerAddress")
    public Mono<Long> registerEmail(RegisterEmailCommand cmd, SagaContext ctx) {
        if (cmd == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get("partyId");
        return customersClient
                .createEmail(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getEmailId()));
    }

    public Mono<Void> removeEmail(Long emailId, SagaContext ctx) {
        if (emailId == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get("partyId");
        return customersClient.deleteEmail(partyId, emailId).mapNotNull(HttpEntity::getBody);
    }

}
