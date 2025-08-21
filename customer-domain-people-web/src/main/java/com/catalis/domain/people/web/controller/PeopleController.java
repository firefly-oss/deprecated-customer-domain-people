package com.catalis.domain.people.web.controller;

import com.catalis.domain.people.core.service.PersonCommandService;
import com.catalis.domain.people.core.service.PersonQueryService;
import com.catalis.domain.people.core.service.exceptions.DuplicateTaxIdException;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterCustomerCommand;
import com.catalis.domain.people.interfaces.dto.query.PersonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "CQ queries and registration for customers")
public class PeopleController {

    private final PersonQueryService personQueryService;
    private final PersonCommandService personCommandService;

    @PostMapping
    @Operation(summary = "Register a customer", description = "Registers a customer")
    public Mono<ResponseEntity<Object>> registerCustomer(@Valid @RequestBody RegisterCustomerCommand command) {
        return personCommandService.register(command)
                .thenReturn(ResponseEntity.noContent().build())
                .onErrorResume(DuplicateTaxIdException.class,
                        ex -> Mono.just(ResponseEntity.status(409).body(null)));
    }


//    @GetMapping("/{customerId}")
//    @Operation(summary = "Get customer by id", description = "Returns a consolidated customer profile (mock)")
//    public Mono<ResponseEntity<PersonView>> getCustomerById(@PathVariable("customerId") Long customerId) {
//        return personQueryService.getById(customerId)
//                .map(ResponseEntity::ok)
//                .defaultIfEmpty(ResponseEntity.notFound().build());
//    }
}
