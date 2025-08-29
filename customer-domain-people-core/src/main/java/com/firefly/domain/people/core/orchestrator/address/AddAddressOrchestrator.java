package com.firefly.domain.people.core.orchestrator.address;

import com.firefly.domain.people.core.integration.client.CustomersClient;
import com.firefly.domain.people.interfaces.dto.commands.RegisterAddressCommand;
import com.firefly.domain.people.interfaces.dto.commands.RemoveAddressCommand;
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
import static com.firefly.domain.people.core.orchestrator.address.AddressConstants.*;

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
@Saga(name = SAGA_ADD_ADDRESS_NAME)
@Service
public class AddAddressOrchestrator {

    private final CustomersClient customersClient;

    @Autowired
    public AddAddressOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = STEP_ADD_NEW_ADDRESS)
    @StepEvent(type = EVENT_ADDRESS_ADDED)
    public Mono<Long> registerAddress(RegisterAddressCommand cmd, SagaContext ctx) {
        return cmd == null
                ? Mono.empty()
                : customersClient.createAddress(cmd.partyId(), cmd)
                .mapNotNull(r -> Objects.requireNonNull(Objects.requireNonNull(r.getBody()).getAddressId()))
                .doOnNext(partyId -> ctx.variables().put(CTX_PARTY_ID, partyId));
    }

}
