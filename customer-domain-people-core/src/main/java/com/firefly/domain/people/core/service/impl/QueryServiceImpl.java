package com.firefly.domain.people.core.service.impl;

import com.firefly.domain.people.core.service.QueryService;
import com.firefly.domain.people.interfaces.dto.query.PersonView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    @Override
    public Mono<PersonView> getCustomerById(Long customerId) {
        // TODO: Implement mark deceased logic
        return Mono.empty();
    }
}