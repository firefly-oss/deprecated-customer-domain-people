package com.catalis.domain.people.core.orchestrator;

import com.catalis.common.domain.events.outbound.EventPublisher;
import com.catalis.domain.people.core.integration.client.CustomersClient;
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
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyProviderCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyRelationshipCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyGroupMembershipCommand;
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

/**
 * Saga orchestrator for customer registration processes.
 * 
 * This orchestrator manages the distributed transaction for registering customers,
 * coordinating multiple steps including party creation, person details registration,
 * contact information setup, and relationship establishment. Each step is designed
 * to be compensatable to ensure data consistency in case of failures.
 * 
 * The orchestrator handles both natural persons and legal entities, with conditional
 * logic to process only relevant information based on the customer type.
 */
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
    @StepEvent(type = "party.registered")
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
    @StepEvent(type = "naturalperson.registered")
    public Mono<Long> registerNaturalPerson(RegisterNaturalPersonCommand cmd, SagaContext ctx) {
        return !ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.createNaturalPerson((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getNaturalPersonId()));
    }

    public Mono<Void> removeNaturalPerson(Long naturalPersonId, SagaContext ctx) {
        return !ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.deleteNaturalPerson((Long) ctx.variables().get(PARTY_ID), naturalPersonId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerLegalPerson", compensate = "removeLegalPerson", dependsOn = "registerParty")
    @StepEvent(type = "legalperson.registered")
    public Mono<Long> registerLegalPerson(RegisterLegalPersonCommand cmd, SagaContext ctx) {
        return !ctx.variables().get(X_CUSTOMER_TYPE).equals(LEGAL_PERSON)
                ? Mono.empty()
                : customersClient.createLegalPerson((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getLegalEntityId()));
    }

    public Mono<Void> removeLegalPerson(Long legalPersonId, SagaContext ctx) {
        return !ctx.variables().get(X_CUSTOMER_TYPE).equals(LEGAL_PERSON)
                ? Mono.empty()
                : customersClient.deleteLegalPerson((Long) ctx.variables().get(PARTY_ID), legalPersonId).mapNotNull(HttpEntity::getBody);
    }


    @SagaStep(id = "registerStatusEntry", compensate = "removeStatusEntry", dependsOn = {"registerNaturalPerson", "registerLegalPerson"})
    @StepEvent(type = "partystatus.registered")
    public Mono<Long> registerStatusEntry(RegisterPartyStatusEntryCommand cmd, SagaContext ctx) {
        return customersClient.createPartyStatus((Long) ctx.variables().get(PARTY_ID), cmd)
                .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPartyStatusId()));
    }

    public Mono<Void> removeStatusEntry(Long id, SagaContext ctx) {
        return customersClient.deletePartyStatus((Long) ctx.variables().get(PARTY_ID), id).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerPep", compensate = "removePep", dependsOn = "registerStatusEntry")
    @StepEvent(type = "pep.registered")
    public Mono<Long> registerPep(RegisterPepCommand cmd, SagaContext ctx) {
        return cmd == null || !ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.createPep((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPepId()));
    }

    public Mono<Void> removePep(Long pepId, SagaContext ctx) {
        return pepId == null || !ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.deletePep((Long) ctx.variables().get(PARTY_ID), pepId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerIdentityDocument", compensate = "removeIdentityDocument", dependsOn = "registerPep")
    @StepEvent(type = "identitydocument.registered")
    public Mono<Long> registerIdentityDocument(RegisterIdentityDocumentCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createIdentityDocument((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getIdentityDocumentId()));
    }

    public Mono<Void> removeIdentityDocument(Long identityDocumentId, SagaContext ctx) {
        return identityDocumentId == null
                ? Mono.empty()
                : customersClient.deleteIdentityDocument((Long) ctx.variables().get(PARTY_ID), identityDocumentId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerAddress", compensate = "removeAddress", dependsOn = "registerIdentityDocument")
    @StepEvent(type = "address.registered")
    public Mono<Long> registerAddress(RegisterAddressCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createAddress((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getAddressId()));
    }

    public Mono<Void> removeAddress(Long addressId, SagaContext ctx) {
        return addressId == null
                ? Mono.empty()
                : customersClient.deleteAddress((Long) ctx.variables().get(PARTY_ID), addressId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerEmail", compensate = "removeEmail", dependsOn = "registerAddress")
    @StepEvent(type = "email.registered")
    public Mono<Long> registerEmail(RegisterEmailCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createEmail((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getEmailContactId()));
    }

    public Mono<Void> removeEmail(Long emailId, SagaContext ctx) {
        return emailId == null
                ? Mono.empty()
                : customersClient.deleteEmail((Long) ctx.variables().get(PARTY_ID), emailId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerPhone", compensate = "removePhone", dependsOn = "registerEmail")
    @StepEvent(type = "phone.registered")
    public Mono<Long> registerPhone(RegisterPhoneCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPhone((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPhoneContactId()));
    }

    public Mono<Void> removePhone(Long phoneId, SagaContext ctx) {
        return phoneId == null
                ? Mono.empty()
                : customersClient.deletePhone((Long) ctx.variables().get(PARTY_ID), phoneId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerEconomicActivityLink", compensate = "removeEconomicActivityLink", dependsOn = {"registerPhone"})
    @StepEvent(type = "economicactivity.registered")
    public Mono<Long> registerEconomicActivityLink(RegisterEconomicActivityLinkCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPartyEconomicActivity((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPartyEconomicActivityId()));
    }

    public Mono<Void> removeEconomicActivityLink(Long id, SagaContext ctx) {
        return id == null
                ? Mono.empty()
                : customersClient.deletePartyEconomicActivity((Long) ctx.variables().get(PARTY_ID), id).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerConsent", compensate = "removeConsent", dependsOn = "registerEconomicActivityLink")
    @StepEvent(type = "consent.registered")
    public Mono<Long> registerConsent(RegisterConsentCommand cmd, SagaContext ctx) {
        return cmd == null || !ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.createConsent((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getConsentId()));
    }

    public Mono<Void> removeConsent(Long consentId, SagaContext ctx) {
        return consentId == null || !ctx.variables().get(X_CUSTOMER_TYPE).equals(NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.deleteConsent((Long) ctx.variables().get(PARTY_ID), consentId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerPartyProvider", compensate = "removePartyProvider", dependsOn = "registerConsent")
    @StepEvent(type = "partyprovider.registered")
    public Mono<Long> registerPartyProvider(RegisterPartyProviderCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPartyProvider((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPartyProviderId()));
    }

    public Mono<Void> removePartyProvider(Long partyProviderId, SagaContext ctx) {
        return partyProviderId == null
                ? Mono.empty()
                : customersClient.deletePartyProvider((Long) ctx.variables().get(PARTY_ID), partyProviderId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerPartyRelationship", compensate = "removePartyRelationship", dependsOn = "registerPartyProvider")
    @StepEvent(type = "partyrelationship.registered")
    public Mono<Long> registerPartyRelationship(RegisterPartyRelationshipCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPartyRelationshipWithHttpInfo((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPartyRelationshipId()));
    }

    public Mono<Void> removePartyRelationship(Long partyRelationshipId, SagaContext ctx) {
        return partyRelationshipId == null
                ? Mono.empty()
                : customersClient.deletePartyRelationshipWithHttpInfo((Long) ctx.variables().get(PARTY_ID), partyRelationshipId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = "registerPartyGroupMembership", compensate = "removePartyGroupMembership", dependsOn = "registerPartyRelationship")
    @StepEvent(type = "partygroupmembership.registered")
    public Mono<Long> registerPartyGroupMembership(RegisterPartyGroupMembershipCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPartyGroupMembershipWithHttpInfo((Long) ctx.variables().get(PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPartyGroupMembershipId()));
    }

    public Mono<Void> removePartyGroupMembership(Long partyGroupMembershipId, SagaContext ctx) {
        return partyGroupMembershipId == null
                ? Mono.empty()
                : customersClient.deletePartyGroupMembershipWithHttpInfo((Long) ctx.variables().get(PARTY_ID), partyGroupMembershipId).mapNotNull(HttpEntity::getBody);
    }
}
