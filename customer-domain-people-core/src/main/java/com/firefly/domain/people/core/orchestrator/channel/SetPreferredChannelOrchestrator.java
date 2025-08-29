package com.firefly.domain.people.core.orchestrator.channel;

import com.firefly.domain.people.core.integration.client.CustomersClient;
import com.firefly.domain.people.interfaces.dto.commands.PreferredChannelCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterEmailCommand;
import com.firefly.domain.people.interfaces.dto.commands.RegisterPhoneCommand;
import com.firefly.transactionalengine.annotations.Saga;
import com.firefly.transactionalengine.annotations.SagaStep;
import com.firefly.transactionalengine.annotations.StepEvent;
import com.firefly.transactionalengine.core.SagaContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.firefly.domain.people.core.orchestrator.channel.ChannelConstants.*;

/**
 * Saga orchestrator for preferred channel update processes.
 * 
 * This orchestrator manages the distributed transaction for updating customer preferred channels,
 * coordinating the update of email and phone preferences and ensuring data consistency in case of failures.
 */
@Saga(name = SAGA_SET_PREFERRED_CHANNEL_NAME)
@Service
public class SetPreferredChannelOrchestrator {

    private final CustomersClient customersClient;

    @Autowired
    public SetPreferredChannelOrchestrator(CustomersClient customersClient) {
        this.customersClient = customersClient;
    }

    @SagaStep(id = STEP_UPDATE_CHANNEL)
    @StepEvent(type = EVENT_PREFERRED_CHANNEL_UPDATED)
    public Mono<Void> updateChannel(PreferredChannelCommand cmd, SagaContext ctx) {

        Mono<Void> emailUpdate = Mono.empty();
        Mono<Void> phoneUpdate = Mono.empty();

        Long partyId = cmd.partyId();
        if (cmd.emailId() != null) {
            emailUpdate = customersClient.updateEmail(partyId, cmd.emailId(),
                            new RegisterEmailCommand(partyId, null, null, true, null))
                    .then();
        }
        if (cmd.phoneId() != null) {
            phoneUpdate = customersClient.updatePhone(partyId, cmd.phoneId(),
                    new RegisterPhoneCommand(partyId, null, null, true, null, null))
                    .then();
        }

        return Mono.when(emailUpdate, phoneUpdate);
    }
}