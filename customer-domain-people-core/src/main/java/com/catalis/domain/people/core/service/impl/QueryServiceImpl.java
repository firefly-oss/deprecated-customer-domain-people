package com.catalis.domain.people.core.service.impl;

import com.catalis.domain.people.core.service.QueryService;
import com.catalis.domain.people.interfaces.dto.query.PersonView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    @Override
    public Mono<PersonView> getCustomerById(Long customerId) {
        // Return mocked data for the customer
        PersonView mockPersonView = PersonView.builder()
                .id(customerId)
                .taxId("12345678901")
                .name("Mock Customer Name")
                .birthDate(LocalDate.of(1990, 1, 15))
                .email("mock.customer@example.com")
                .build();
        
        return Mono.just(mockPersonView);
    }
}