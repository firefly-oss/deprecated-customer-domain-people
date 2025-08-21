package com.catalis.domain.people.core.service.impl;

import com.catalis.domain.people.core.service.PersonCommandService;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterCustomerCommand;
import com.catalis.transactionalengine.core.SagaContext;
import com.catalis.transactionalengine.core.SagaResult;
import com.catalis.transactionalengine.engine.SagaEngine;
import com.catalis.transactionalengine.engine.StepInputs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                .forStepId("registerParty", command.party())
                .forStepId("registerNaturalPerson", command.naturalPerson())
                .build();

        return engine.execute("RegisterCustomerSaga", inputs);
    }
}