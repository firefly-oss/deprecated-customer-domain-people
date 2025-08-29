package com.firefly.domain.people.core.orchestrator.customer;

import com.firefly.domain.people.core.integration.client.CustomersClient;
import com.firefly.domain.people.interfaces.dto.commands.RegisterLegalPersonCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterNaturalPersonCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterPartyCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterPartyStatusEntryCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterPepCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterIdentityDocumentCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterAddressCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterEmailCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterPhoneCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterEconomicActivityLinkCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterConsentCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterPartyProviderCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterPartyRelationshipCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterPartyGroupMembershipCommand;
import com.firefly.transactionalengine.annotations.Saga;
import com.firefly.transactionalengine.annotations.SagaStep;
import com.firefly.transactionalengine.annotations.StepEvent;
import com.firefly.transactionalengine.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.firefly.domain.people.core.orchestrator.GlobalConstants.*;
import static com.firefly.domain.people.core.orchestrator.customer.RegisterCustomerConstants.*;

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
@Saga(name = SAGA_REGISTER_CUSTOMER_NAME)
@Service
public class RegisterCustomerOrchestrator {

    private final CustomersClient customersClient;

    @Autowired
    public RegisterCustomerOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = STEP_REGISTER_PARTY, compensate = COMPENSATE_REMOVE_PARTY)
    @StepEvent(type = EVENT_PARTY_REGISTERED)
    public Mono<Long> registerParty(RegisterPartyCommand cmd, SagaContext ctx) {
        ctx.variables().put(CTX_CUSTOMER_TYPE, cmd.partyKind());
        return customersClient
                .createParty(cmd)
                .mapNotNull(partyDTOResponseEntity ->
                        Objects.requireNonNull(Objects.requireNonNull(partyDTOResponseEntity.getBody()).getPartyId()))
                .doOnNext(partyId -> ctx.variables().put(CTX_PARTY_ID, partyId));
    }

    public Mono<Void> removeParty(Long partyId) {
        return customersClient.deleteParty(partyId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_NATURAL_PERSON, compensate = COMPENSATE_REMOVE_NATURAL_PERSON, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_NATURAL_PERSON_REGISTERED)
    public Mono<Long> registerNaturalPerson(RegisterNaturalPersonCommand cmd, SagaContext ctx) {
        return !ctx.variables().get(CTX_CUSTOMER_TYPE).equals(TYPE_NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.createNaturalPerson((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getNaturalPersonId()));
    }

    public Mono<Void> removeNaturalPerson(Long naturalPersonId, SagaContext ctx) {
        return !ctx.variables().get(CTX_CUSTOMER_TYPE).equals(TYPE_NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.deleteNaturalPerson((Long) ctx.variables().get(CTX_PARTY_ID), naturalPersonId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_LEGAL_PERSON, compensate = COMPENSATE_REMOVE_LEGAL_PERSON, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_LEGAL_PERSON_REGISTERED)
    public Mono<Long> registerLegalPerson(RegisterLegalPersonCommand cmd, SagaContext ctx) {
        return !ctx.variables().get(CTX_CUSTOMER_TYPE).equals(TYPE_LEGAL_ENTITY)
                ? Mono.empty()
                : customersClient.createLegalPerson((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getLegalEntityId()));
    }

    public Mono<Void> removeLegalPerson(Long legalPersonId, SagaContext ctx) {
        return !ctx.variables().get(CTX_CUSTOMER_TYPE).equals(TYPE_LEGAL_ENTITY)
                ? Mono.empty()
                : customersClient.deleteLegalEntity((Long) ctx.variables().get(CTX_PARTY_ID), legalPersonId).mapNotNull(HttpEntity::getBody);
    }


    @SagaStep(id = STEP_REGISTER_STATUS_ENTRY, compensate = COMPENSATE_REMOVE_STATUS_ENTRY, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PARTY_STATUS_REGISTERED)
    public Mono<Long> registerStatusEntry(RegisterPartyStatusEntryCommand cmd, SagaContext ctx) {
        return customersClient.createPartyStatus((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPartyStatusId()));
    }

    public Mono<Void> removeStatusEntry(Long id, SagaContext ctx) {
        return customersClient.deletePartyStatus((Long) ctx.variables().get(CTX_PARTY_ID), id).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_PEP, compensate = COMPENSATE_REMOVE_PEP, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PEP_REGISTERED)
    public Mono<Long> registerPep(RegisterPepCommand cmd, SagaContext ctx) {
        return cmd == null || !ctx.variables().get(CTX_CUSTOMER_TYPE).equals(TYPE_NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.createPep((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPepId()));
    }

    public Mono<Void> removePep(Long pepId, SagaContext ctx) {
        return pepId == null || !ctx.variables().get(CTX_CUSTOMER_TYPE).equals(TYPE_NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.deletePep((Long) ctx.variables().get(CTX_PARTY_ID), pepId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_IDENTITY_DOCUMENT, compensate = COMPENSATE_REMOVE_IDENTITY_DOCUMENT, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_IDENTITY_DOCUMENT_REGISTERED)
    public Mono<Long> registerIdentityDocument(RegisterIdentityDocumentCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createIdentityDocument((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getIdentityDocumentId()));
    }

    public Mono<Void> removeIdentityDocument(Long identityDocumentId, SagaContext ctx) {
        return identityDocumentId == null
                ? Mono.empty()
                : customersClient.deleteIdentityDocument((Long) ctx.variables().get(CTX_PARTY_ID), identityDocumentId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_ADDRESS, compensate = COMPENSATE_REMOVE_ADDRESS, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_ADDRESS_REGISTERED)
    public Mono<Long> registerAddress(RegisterAddressCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createAddress((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getAddressId()));
    }

    public Mono<Void> removeAddress(Long addressId, SagaContext ctx) {
        return addressId == null
                ? Mono.empty()
                : customersClient.deleteAddress((Long) ctx.variables().get(CTX_PARTY_ID), addressId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_EMAIL, compensate = COMPENSATE_REMOVE_EMAIL, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_EMAIL_REGISTERED)
    public Mono<Long> registerEmail(RegisterEmailCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createEmail((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getEmailContactId()));
    }

    public Mono<Void> removeEmail(Long emailId, SagaContext ctx) {
        return emailId == null
                ? Mono.empty()
                : customersClient.deleteEmail((Long) ctx.variables().get(CTX_PARTY_ID), emailId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_PHONE, compensate = COMPENSATE_REMOVE_PHONE, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PHONE_REGISTERED)
    public Mono<Long> registerPhone(RegisterPhoneCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPhone((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPhoneContactId()));
    }

    public Mono<Void> removePhone(Long phoneId, SagaContext ctx) {
        return phoneId == null
                ? Mono.empty()
                : customersClient.deletePhone((Long) ctx.variables().get(CTX_PARTY_ID), phoneId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_ECONOMIC_ACTIVITY_LINK, compensate = COMPENSATE_REMOVE_ECONOMIC_ACTIVITY_LINK, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_ECONOMIC_ACTIVITY_REGISTERED)
    public Mono<Long> registerEconomicActivityLink(RegisterEconomicActivityLinkCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPartyEconomicActivity((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPartyEconomicActivityId()));
    }

    public Mono<Void> removeEconomicActivityLink(Long id, SagaContext ctx) {
        return id == null
                ? Mono.empty()
                : customersClient.deletePartyEconomicActivity((Long) ctx.variables().get(CTX_PARTY_ID), id).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_CONSENT, compensate = COMPENSATE_REMOVE_CONSENT, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_CONSENT_REGISTERED)
    public Mono<Long> registerConsent(RegisterConsentCommand cmd, SagaContext ctx) {
        return cmd == null || !ctx.variables().get(CTX_CUSTOMER_TYPE).equals(TYPE_NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.createConsent((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getConsentId()));
    }

    public Mono<Void> removeConsent(Long consentId, SagaContext ctx) {
        return consentId == null || !ctx.variables().get(CTX_CUSTOMER_TYPE).equals(TYPE_NATURAL_PERSON)
                ? Mono.empty()
                : customersClient.deleteConsent((Long) ctx.variables().get(CTX_PARTY_ID), consentId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_PARTY_PROVIDER, compensate = COMPENSATE_REMOVE_PARTY_PROVIDER, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PARTY_PROVIDER_REGISTERED)
    public Mono<Long> registerPartyProvider(RegisterPartyProviderCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPartyProvider((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPartyProviderId()));
    }

    public Mono<Void> removePartyProvider(Long partyProviderId, SagaContext ctx) {
        return partyProviderId == null
                ? Mono.empty()
                : customersClient.deletePartyProvider((Long) ctx.variables().get(CTX_PARTY_ID), partyProviderId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_PARTY_RELATIONSHIP, compensate = COMPENSATE_REMOVE_PARTY_RELATIONSHIP, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PARTY_RELATIONSHIP_REGISTERED)
    public Mono<Long> registerPartyRelationship(RegisterPartyRelationshipCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPartyRelationshipWithHttpInfo((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPartyRelationshipId()));
    }

    public Mono<Void> removePartyRelationship(Long partyRelationshipId, SagaContext ctx) {
        return partyRelationshipId == null
                ? Mono.empty()
                : customersClient.deletePartyRelationshipWithHttpInfo((Long) ctx.variables().get(CTX_PARTY_ID), partyRelationshipId).mapNotNull(HttpEntity::getBody);
    }

    @SagaStep(id = STEP_REGISTER_PARTY_GROUP_MEMBERSHIP, compensate = COMPENSATE_REMOVE_PARTY_GROUP_MEMBERSHIP, dependsOn = STEP_REGISTER_PARTY)
    @StepEvent(type = EVENT_PARTY_GROUP_MEMBERSHIP_REGISTERED)
    public Mono<Long> registerPartyGroupMembership(RegisterPartyGroupMembershipCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPartyGroupMembershipWithHttpInfo((Long) ctx.variables().get(CTX_PARTY_ID), cmd)
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPartyGroupMembershipId()));
    }

    public Mono<Void> removePartyGroupMembership(Long partyGroupMembershipId, SagaContext ctx) {
        return partyGroupMembershipId == null
                ? Mono.empty()
                : customersClient.deletePartyGroupMembershipWithHttpInfo((Long) ctx.variables().get(CTX_PARTY_ID), partyGroupMembershipId).mapNotNull(HttpEntity::getBody);
    }
}
