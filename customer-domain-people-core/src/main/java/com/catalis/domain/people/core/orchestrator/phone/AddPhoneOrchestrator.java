package com.catalis.domain.people.core.orchestrator.phone;

import com.catalis.domain.people.core.integration.client.CustomersClient;
import com.catalis.domain.people.interfaces.dto.commands.RegisterPhoneCommand;
import com.catalis.transactionalengine.annotations.Saga;
import com.catalis.transactionalengine.annotations.SagaStep;
import com.catalis.transactionalengine.annotations.StepEvent;
import com.catalis.transactionalengine.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.catalis.domain.people.core.orchestrator.GlobalConstants.CTX_PARTY_ID;
import static com.catalis.domain.people.core.orchestrator.phone.PhoneConstants.*;

/**
 * Saga orchestrator for phone registration processes.
 * 
 * This orchestrator manages the distributed transaction for registering phones,
 * coordinating the phone creation step and providing compensation logic
 * to ensure data consistency in case of failures.
 * 
 * Each step is designed to be compensatable to ensure data consistency
 * in case of failures during the phone registration process.
 */
@Saga(name = SAGA_ADD_PHONE_NAME)
@Service
public class AddPhoneOrchestrator {

    private final CustomersClient customersClient;

    @Autowired
    public AddPhoneOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = STEP_ADD_NEW_PHONE)
    @StepEvent(type = EVENT_PHONE_ADDED)
    public Mono<Long> registerPhone(RegisterPhoneCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createPhone(cmd.partyId(), cmd)
                .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getPhoneContactId()))
                .doOnNext(phoneId -> ctx.variables().put(CTX_PARTY_ID, cmd.partyId()));
    }

    public Mono<Void> removePhone(Long phoneId, SagaContext ctx) {
        return phoneId == null
                ? Mono.empty()
                : customersClient.deletePhone((Long) ctx.variables().get(CTX_PARTY_ID), phoneId).mapNotNull(HttpEntity::getBody);
    }
}