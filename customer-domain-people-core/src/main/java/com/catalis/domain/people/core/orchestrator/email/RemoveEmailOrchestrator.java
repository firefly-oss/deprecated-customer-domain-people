package com.catalis.domain.people.core.orchestrator.email;

import com.catalis.domain.people.core.integration.client.CustomersClient;
import com.catalis.domain.people.interfaces.dto.commands.RemoveEmailCommand;
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
 * Saga orchestrator for email removal processes.
 * 
 * This orchestrator manages the distributed transaction for removing emails,
 * coordinating the email deletion step. The step is designed to be compensatable 
 * to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_REMOVE_EMAIL_NAME)
@Service
public class RemoveEmailOrchestrator {

    private final CustomersClient customersClient;

    @Autowired
    public RemoveEmailOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = STEP_REMOVE_NEW_EMAIL)
    @StepEvent(type = EVENT_EMAIL_REMOVED)
    public Mono<Void> removeEmail(RemoveEmailCommand cmd) {
        return cmd == null
                ? Mono.empty()
                : customersClient.deleteEmail(cmd.partyId(), cmd.emailId()).mapNotNull(HttpEntity::getBody);
    }
}