package com.catalis.domain.people.core.orchestrator.customer;

import com.catalis.domain.people.core.integration.client.CustomersClient;
import com.catalis.domain.people.core.integration.mapper.CustomersMapper;
import com.catalis.domain.people.interfaces.dto.commands.*;
import com.catalis.domain.people.interfaces.dto.query.PartyView;
import com.catalis.transactionalengine.annotations.FromStep;
import com.catalis.transactionalengine.annotations.Saga;
import com.catalis.transactionalengine.annotations.SagaStep;
import com.catalis.transactionalengine.annotations.StepEvent;
import com.catalis.transactionalengine.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.catalis.domain.people.core.orchestrator.GlobalConstants.*;
import static com.catalis.domain.people.core.orchestrator.customer.RegisterCustomerConstants.*;

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
@Saga(name = SAGA_UPDATE_LEGAL_NAME_NAME)
@Service
public class UpdateNameOrchestrator {

    private final CustomersClient customersClient;
    private final CustomersMapper customersMapper;

    @Autowired
    public UpdateNameOrchestrator(CustomersClient customersClient, CustomersMapper customersMapper) {
        this.customersClient = customersClient;
        this.customersMapper = customersMapper;
    }

    @SagaStep(id = STEP_RETRIEVE_PARTY_ID)
    @StepEvent(type = EVENT_PARTY_RETRIEVED)
    public Mono<PartyView> retrievePartyId(UpdateNameCommand cmd, SagaContext ctx) {
        return customersClient
                .getParty(cmd.partyId())
                .mapNotNull(partyDTOResponseEntity ->
                        customersMapper.toPartyView(Objects.requireNonNull(partyDTOResponseEntity.getBody())));
    }

    @SagaStep(id = STEP_RETRIEVE_CUSTOMER, dependsOn = STEP_RETRIEVE_PARTY_ID)
    @StepEvent(type = EVENT_CUSTOMER_RETRIEVED)
    public Mono<Long> retrieveCustomer(UpdateNameCommand cmd, SagaContext ctx, @FromStep(STEP_RETRIEVE_PARTY_ID) PartyView partyView) {
        if (TYPE_NATURAL_PERSON.equals(partyView.getPartyKind())) {
            return customersClient
                    .getNaturalPerson(cmd.partyId())
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getNaturalPersonId()))
                    .doOnNext(naturalPersonId -> ctx.variables().put(CTX_CUSTOMER_ID, naturalPersonId));
        } else if (TYPE_LEGAL_ENTITY.equals(partyView.getPartyKind())) {
            return customersClient
                    .getLegalEntity(cmd.partyId())
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getLegalEntityId()))
                    .doOnNext(legalEntityId -> ctx.variables().put(CTX_CUSTOMER_ID, legalEntityId));
        } else {
            return Mono.error(new IllegalArgumentException("Unsupported party kind: " + partyView.getPartyKind()));
        }
    }

    @SagaStep(id = STEP_UPDATE_CUSTOMER_NAME, dependsOn = STEP_RETRIEVE_CUSTOMER)
    @StepEvent(type = EVENT_CUSTOMER_NAME_CHANGED)
    public Mono<Long> updateName(UpdateNameCommand cmd, SagaContext ctx, @FromStep(STEP_RETRIEVE_PARTY_ID) PartyView partyView) {
        if (TYPE_NATURAL_PERSON.equals(partyView.getPartyKind())) {
            return customersClient
                    .updateNaturalPerson(cmd.partyId(), (Long)ctx.variables().get(CTX_CUSTOMER_ID), cmd.newName())
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getNaturalPersonId()));
        } else if (TYPE_LEGAL_ENTITY.equals(partyView.getPartyKind())) {
            return customersClient
                    .updateLegalEntity(cmd.partyId(), (Long)ctx.variables().get(CTX_CUSTOMER_ID), cmd.newName())
                    .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getLegalEntityId()));
        } else {
            return Mono.error(new IllegalArgumentException("Unsupported party kind: " + partyView.getPartyKind()));
        }
    }


}
