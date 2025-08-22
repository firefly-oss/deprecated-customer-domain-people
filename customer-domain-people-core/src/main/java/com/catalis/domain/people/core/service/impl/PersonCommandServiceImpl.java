package com.catalis.domain.people.core.service.impl;

import com.catalis.domain.people.core.orchestrator.RegisterCustomerOrchestrator;
import com.catalis.domain.people.core.service.PersonCommandService;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterCustomerCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyStatusEntryCommand;
import com.catalis.transactionalengine.core.SagaContext;
import com.catalis.transactionalengine.core.SagaResult;
import com.catalis.transactionalengine.engine.ExpandEach;
import com.catalis.transactionalengine.engine.SagaEngine;
import com.catalis.transactionalengine.engine.StepInputs;
import com.catalis.transactionalengine.registry.SagaBuilder;
import com.catalis.transactionalengine.registry.SagaDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class PersonCommandServiceImpl implements PersonCommandService {

    private final SagaEngine engine;

    @Autowired
    public PersonCommandServiceImpl(SagaEngine engine) {
        this.engine = engine;
    }

    @Override
    public Mono<SagaResult> register(RegisterCustomerCommand command) {

        StepInputs inputs = StepInputs.builder()
                .forStep(RegisterCustomerOrchestrator::registerParty, command.party())
                .forStep(RegisterCustomerOrchestrator::registerNaturalPerson, command.naturalPerson())
                .forStep(RegisterCustomerOrchestrator::registerLegalPerson, command.legalPerson())
                .forStep(RegisterCustomerOrchestrator::registerStatusEntry, ExpandEach.of(command.statusHistory()))
                .forStep(RegisterCustomerOrchestrator::registerPep, command.pep())
                .forStep(RegisterCustomerOrchestrator::registerIdentityDocument, ExpandEach.of(command.identityDocuments()))
                .forStep(RegisterCustomerOrchestrator::registerAddress, ExpandEach.of(command.addresses()))
                .forStep(RegisterCustomerOrchestrator::registerEmail, ExpandEach.of(command.emails()))
                .build();

        return engine.execute(RegisterCustomerOrchestrator.class, inputs);
    }
}