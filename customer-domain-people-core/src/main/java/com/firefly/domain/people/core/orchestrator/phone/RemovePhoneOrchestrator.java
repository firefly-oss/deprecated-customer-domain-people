package com.firefly.domain.people.core.orchestrator.phone;

import com.firefly.domain.people.core.integration.client.CustomersClient;
import com.firefly.domain.people.interfaces.dto.commands.RemovePhoneCommand;
import com.firefly.transactionalengine.annotations.Saga;
import com.firefly.transactionalengine.annotations.SagaStep;
import com.firefly.transactionalengine.annotations.StepEvent;
import com.firefly.transactionalengine.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.firefly.domain.people.core.orchestrator.GlobalConstants.CTX_PARTY_ID;
import static com.firefly.domain.people.core.orchestrator.phone.PhoneConstants.*;

/**
 * Saga orchestrator for phone removal processes.
 * 
 * This orchestrator manages the distributed transaction for removing phones,
 * coordinating the phone deletion step. The step is designed to be compensatable 
 * to ensure data consistency in case of failures.
 */
@Saga(name = SAGA_REMOVE_PHONE_NAME)
@Service
public class RemovePhoneOrchestrator {

    private final CustomersClient customersClient;

    @Autowired
    public RemovePhoneOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = STEP_REMOVE_NEW_PHONE)
    @StepEvent(type = EVENT_PHONE_REMOVED)
    public Mono<Void> removePhone(RemovePhoneCommand cmd) {
        return cmd == null
                ? Mono.empty()
                : customersClient.deletePhone(cmd.partyId(), cmd.phoneId()).mapNotNull(HttpEntity::getBody);
    }
}