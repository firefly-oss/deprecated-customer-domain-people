package com.firefly.domain.people.core.orchestrator.status;

import com.firefly.domain.people.core.integration.client.CustomersClient;
import com.firefly.domain.people.interfaces.dto.commands.RegisterPartyStatusEntryCommand;
import com.firefly.transactionalengine.annotations.Saga;
import com.firefly.transactionalengine.annotations.SagaStep;
import com.firefly.transactionalengine.annotations.StepEvent;
import com.firefly.transactionalengine.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.firefly.domain.people.core.orchestrator.status.StatusConstants.*;

/**
 * Saga orchestrator for status update processes.
 * 
 * This orchestrator manages the distributed transaction for updating customer status,
 * coordinating the update operation and ensuring data consistency in case of failures.
 */
@Saga(name = SAGA_UPDATE_STATUS_NAME)
@Service
public class UpdateStatusOrchestrator {

    private final CustomersClient customersClient;

    @Autowired
    public UpdateStatusOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = STEP_UPDATE_STATUS)
    @StepEvent(type = EVENT_STATUS_UPDATED)
    public Mono<Void> updateStatus(RegisterPartyStatusEntryCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.updatePartyStatus(cmd.partyId(), cmd)
                .then();
    }
}