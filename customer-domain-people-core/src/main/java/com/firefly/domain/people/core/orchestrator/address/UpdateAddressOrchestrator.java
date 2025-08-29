package com.firefly.domain.people.core.orchestrator.address;

import com.firefly.domain.people.core.integration.client.CustomersClient;
import com.firefly.domain.people.interfaces.dto.commands.RegisterAddressCommand;
import com.firefly.transactionalengine.annotations.Saga;
import com.firefly.transactionalengine.annotations.SagaStep;
import com.firefly.transactionalengine.annotations.StepEvent;
import com.firefly.transactionalengine.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.firefly.domain.people.core.orchestrator.address.AddressConstants.*;

/**
 * Saga orchestrator for address update processes.
 * 
 * This orchestrator manages the distributed transaction for updating customer addresses,
 * coordinating the update operation and ensuring data consistency in case of failures.
 */
@Saga(name = SAGA_UPDATE_ADDRESS_NAME)
@Service
public class UpdateAddressOrchestrator {

    private final CustomersClient customersClient;

    @Autowired
    public UpdateAddressOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = STEP_UPDATE_ADDRESS)
    @StepEvent(type = EVENT_ADDRESS_UPDATED)
    public Mono<Void> updateAddress(RegisterAddressCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.updateAddress(cmd.partyId(), cmd.addressId(), cmd)
                .then();
    }
}