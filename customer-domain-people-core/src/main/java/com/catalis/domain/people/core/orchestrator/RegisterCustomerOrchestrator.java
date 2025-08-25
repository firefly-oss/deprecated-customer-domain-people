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
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPhoneCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterEconomicActivityLinkCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterConsentCommand;
import com.catalis.transactionalengine.annotations.Saga;
import com.catalis.transactionalengine.annotations.SagaStep;
import com.catalis.transactionalengine.annotations.StepEvent;
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
    public static final String LEGAL_PERSON = "ORGANIZATION";
    public static final String NATURAL_PERSON = "INDIVIDUAL";
    public static final String PARTY_ID = "partyId";
    private final CustomersClient customersClient;

    @Autowired
    public RegisterCustomerOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = "registerParty", compensate = "removeParty")
    public Mono<Long> registerParty(RegisterPartyCommand cmd, SagaContext ctx) {
        ctx.variables().put(X_CUSTOMER_TYPE, cmd.partyKind());
        return customersClient
                .createParty(cmd)
                .mapNotNull(partyDTOResponseEntity ->
                        Objects.requireNonNull(Objects.requireNonNull(partyDTOResponseEntity.getBody()).getPartyId()))
                .doOnNext(partyId -> ctx.variables().put(PARTY_ID, partyId));
    }

    public Mono<Void> removeParty(Long partyId) {
        return customersClient.deleteParty(partyId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerNaturalPerson", compensate = "removeNaturalPerson", dependsOn = "registerParty")
    public Mono<Long> registerNaturalPerson(RegisterNaturalPersonCommand cmd, SagaContext ctx) {
        if(!ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)){
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient
                .createNaturalPerson(partyId, cmd)
                .mapNotNull(partyDTOResponseEntity ->
                        Objects.requireNonNull(Objects.requireNonNull(partyDTOResponseEntity.getBody()).getNaturalPersonId()));
    }

    public Mono<Void> removeNaturalPerson(Long naturalPersonId, SagaContext ctx) {
        if(!ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)){
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient.deleteNaturalPerson(partyId, naturalPersonId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerLegalPerson", compensate = "removeLegalPerson", dependsOn = "registerParty")
    public Mono<Long> registerLegalPerson(RegisterLegalPersonCommand cmd, SagaContext ctx) {
        if(!ctx.variables().get(X_CUSTOMER_TYPE).equals("LEGAL_PERSON")){
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient
                .createLegalPerson(partyId, cmd)
                .mapNotNull(legalPersonDTOResponseEntity ->
                        Objects.requireNonNull(Objects.requireNonNull(legalPersonDTOResponseEntity.getBody()).getLegalEntityId()));
    }

    public Mono<Void> removeLegalPerson(Long legalPersonId, SagaContext ctx) {
        if(!ctx.variables().get(X_CUSTOMER_TYPE).equals(LEGAL_PERSON)){
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient.deleteLegalPerson(partyId, legalPersonId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerStatusEntry", compensate = "removeStatusEntry", dependsOn = {"registerNaturalPerson", "registerLegalPerson"})
    public Mono<Long> registerStatusEntry(RegisterPartyStatusEntryCommand cmd, SagaContext ctx) {
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient
                        .createPartyStatus(partyId, cmd)
                        .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getPartyStatusId()));
    }

    public Mono<Void> removeStatusEntry(Long id, SagaContext ctx) {
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient.deletePartyStatus(partyId, id).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerPep", compensate = "removePep", dependsOn = "registerStatusEntry")
    public Mono<Long> registerPep(RegisterPepCommand cmd, SagaContext ctx) {
        if (cmd == null || !ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient
                .createPep(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getPepId()));
    }

    public Mono<Void> removePep(Long pepId, SagaContext ctx) {
        if (pepId == null || !ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient.deletePep(partyId, pepId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerIdentityDocument", compensate = "removeIdentityDocument", dependsOn = "registerPep")
    public Mono<Long> registerIdentityDocument(RegisterIdentityDocumentCommand cmd, SagaContext ctx) {
        if (cmd == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient
                .createIdentityDocument(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getIdentityDocumentId()));
    }

    public Mono<Void> removeIdentityDocument(Long identityDocumentId, SagaContext ctx) {
        if (identityDocumentId == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient.deleteIdentityDocument(partyId, identityDocumentId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerAddress", compensate = "removeAddress", dependsOn = "registerIdentityDocument")
    public Mono<Long> registerAddress(RegisterAddressCommand cmd, SagaContext ctx) {
        if (cmd == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient
                .createAddress(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getAddressId()));
    }

    public Mono<Void> removeAddress(Long addressId, SagaContext ctx) {
        if (addressId == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient.deleteAddress(partyId, addressId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerEmail", compensate = "removeEmail", dependsOn = "registerAddress")
    public Mono<Long> registerEmail(RegisterEmailCommand cmd, SagaContext ctx) {
        if (cmd == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient
                .createEmail(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getEmailContactId()));
    }

    public Mono<Void> removeEmail(Long emailId, SagaContext ctx) {
        if (emailId == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient.deleteEmail(partyId, emailId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerPhone", compensate = "removePhone", dependsOn = "registerEmail")
    public Mono<Long> registerPhone(RegisterPhoneCommand cmd, SagaContext ctx) {
        if (cmd == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient
                .createPhone(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getPhoneContactId()));
    }

    public Mono<Void> removePhone(Long phoneId, SagaContext ctx) {
        if (phoneId == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient.deletePhone(partyId, phoneId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerEconomicActivityLink", compensate = "removeEconomicActivityLink", dependsOn = {"registerNaturalPerson", "registerLegalPerson"})
    public Mono<Long> registerEconomicActivityLink(RegisterEconomicActivityLinkCommand cmd, SagaContext ctx) {
        if (cmd == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient
                .createPartyEconomicActivity(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getPartyEconomicActivityId()));
    }

    public Mono<Void> removeEconomicActivityLink(Long id, SagaContext ctx) {
        if (id == null) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient.deletePartyEconomicActivity(partyId, id).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerConsent", compensate = "removeConsent", dependsOn = "registerStatusEntry")
    public Mono<Long> registerConsent(RegisterConsentCommand cmd, SagaContext ctx) {
        if (cmd == null || !ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient
                .createConsent(partyId, cmd)
                .mapNotNull(resp -> Objects.requireNonNull(Objects.requireNonNull(resp.getBody()).getConsentId()));
    }

    public Mono<Void> removeConsent(Long consentId, SagaContext ctx) {
        if (consentId == null || !ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)) {
            return Mono.empty();
        }
        Long partyId = (Long) ctx.variables().get(PARTY_ID);
        return customersClient.deleteConsent(partyId, consentId).mapNotNull(HttpEntity::getBody);
    }
}
