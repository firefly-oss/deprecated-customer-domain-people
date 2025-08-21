package com.catalis.domain.people.core.service;

import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterCustomerCommand;
import com.catalis.transactionalengine.core.SagaResult;
import reactor.core.publisher.Mono;

public interface PersonCommandService {
    Mono<SagaResult> register(RegisterCustomerCommand command);
}