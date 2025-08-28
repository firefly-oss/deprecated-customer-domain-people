package com.catalis.domain.people.core.orchestrator.email;

import com.catalis.domain.people.core.integration.client.CustomersClient;
import com.catalis.domain.people.interfaces.dto.commands.RegisterEmailCommand;
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
import static com.catalis.domain.people.core.orchestrator.email.EmailConstants.*;

/**
 * Saga orchestrator for email registration processes.
 * 
 * This orchestrator manages the distributed transaction for registering emails,
 * coordinating the email creation step and providing compensation logic
 * to ensure data consistency in case of failures.
 * 
 * Each step is designed to be compensatable to ensure data consistency
 * in case of failures during the email registration process.
 */
@Saga(name = SAGA_ADD_EMAIL_NAME)
@Service
public class AddEmailOrchestrator {

    private final CustomersClient customersClient;

    @Autowired
    public AddEmailOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = STEP_ADD_NEW_EMAIL)
    @StepEvent(type = EVENT_EMAIL_ADDED)
    public Mono<Long> registerEmail(RegisterEmailCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createEmail(cmd.partyId(), cmd)
                .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getEmailContactId()))
                .doOnNext(emailId -> ctx.variables().put(CTX_PARTY_ID, cmd.partyId()));
    }

    public Mono<Void> removeEmail(Long emailId, SagaContext ctx) {
        return emailId == null
                ? Mono.empty()
                : customersClient.deleteEmail((Long) ctx.variables().get(CTX_PARTY_ID), emailId).mapNotNull(HttpEntity::getBody);
    }
}