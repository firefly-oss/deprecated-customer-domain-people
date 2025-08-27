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

    @PatchMapping("/{customerId}/name")
    @Operation(summary = "Update customer name", description = "Updates the name of an existing customer")
    public Mono<ResponseEntity<Object>> updateCustomerName(
            @PathVariable("customerId") Long customerId, 
            @RequestBody String newName) {
        return personCommandService.updateName(customerId, newName)
                .thenReturn(ResponseEntity.ok().build());
    }


    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by id", description = "Returns a consolidated customer profile")
    public Mono<ResponseEntity<PersonView>> retrieveCustomerInformation(@PathVariable("customerId") Long customerId) {
        return personQueryService.getCustomerById(customerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Address endpoints
    @PostMapping("/{customerId}/addresses")
    @Operation(summary = "Add customer address", description = "Add an address with validity period; prevent primary overlaps.")
    public Mono<ResponseEntity<Object>> addCustomerAddress(
            @PathVariable("customerId") Long customerId, 
            @RequestBody Object addressData) {
        return personCommandService.addAddress(customerId, addressData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PatchMapping("/{customerId}/addresses/{addressId}")
    @Operation(summary = "Update customer address", description = "Modify address fields keeping version history.")
    public Mono<ResponseEntity<Object>> updateCustomerAddress(
            @PathVariable("customerId") Long customerId,
            @PathVariable("addressId") Long addressId,
            @RequestBody Object addressData) {
        return personCommandService.updateAddress(customerId, addressId, addressData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    @Operation(summary = "Remove customer address", description = "Remove or end-date an address; preserve audit.")
    public Mono<ResponseEntity<Object>> removeCustomerAddress(
            @PathVariable("customerId") Long customerId,
            @PathVariable("addressId") Long addressId) {
        return personCommandService.removeAddress(customerId, addressId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Email endpoints
    @PostMapping("/{customerId}/emails")
    @Operation(summary = "Add customer email", description = "Add email; validate format and uniqueness within customer.")
    public Mono<ResponseEntity<Object>> addCustomerEmail(
            @PathVariable("customerId") Long customerId,
            @RequestBody Object emailData) {
        return personCommandService.addEmail(customerId, emailData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{customerId}/emails/{emailId}")
    @Operation(summary = "Remove customer email", description = "Remove email from profile.")
    public Mono<ResponseEntity<Object>> removeCustomerEmail(
            @PathVariable("customerId") Long customerId,
            @PathVariable("emailId") Long emailId) {
        return personCommandService.removeEmail(customerId, emailId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Phone endpoints
    @PostMapping("/{customerId}/phones")
    @Operation(summary = "Add customer phone", description = "Add phone in E.164 with label (mobile/landline).")
    public Mono<ResponseEntity<Object>> addCustomerPhone(
            @PathVariable("customerId") Long customerId,
            @RequestBody Object phoneData) {
        return personCommandService.addPhone(customerId, phoneData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{customerId}/phones/{phoneId}")
    @Operation(summary = "Remove customer phone", description = "Remove phone from profile.")
    public Mono<ResponseEntity<Object>> removeCustomerPhone(
            @PathVariable("customerId") Long customerId,
            @PathVariable("phoneId") Long phoneId) {
        return personCommandService.removePhone(customerId, phoneId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Preferred channel endpoint
    @PostMapping("/{customerId}/preferred-channel")
    @Operation(summary = "Set preferred channel", description = "Set preferred contact channel (email/phone); one per type.")
    public Mono<ResponseEntity<Object>> setPreferredChannel(
            @PathVariable("customerId") Long customerId,
            @RequestBody Object channelData) {
        return personCommandService.setPreferredChannel(customerId, channelData)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Authorized signers endpoints
    @PostMapping("/{customerId}/authorized-signers")
    @Operation(summary = "Add authorized signatory", description = "Grant a person authority scope to operate (links with PartyRelationship).")
    public Mono<ResponseEntity<Object>> addAuthorizedSignatory(
            @PathVariable("customerId") Long customerId,
            @RequestBody Object signatoryData) {
        return personCommandService.addAuthorizedSignatory(customerId, signatoryData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{customerId}/authorized-signers/{partyId}")
    @Operation(summary = "Remove authorized signatory", description = "Revoke authority to operate for the given party.")
    public Mono<ResponseEntity<Object>> removeAuthorizedSignatory(
            @PathVariable("customerId") Long customerId,
            @PathVariable("partyId") Long partyId) {
        return personCommandService.removeAuthorizedSignatory(customerId, partyId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Status management endpoints
    @PostMapping("/{customerId}/dormant")
    @Operation(summary = "Mark customer dormant", description = "Flag profile as dormant due to inactivity.")
    public Mono<ResponseEntity<Object>> markCustomerDormant(@PathVariable("customerId") Long customerId) {
        return personCommandService.markDormant(customerId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{customerId}/reactivate")
    @Operation(summary = "Reactivate customer", description = "Reactivate a dormant profile.")
    public Mono<ResponseEntity<Object>> reactivateCustomer(@PathVariable("customerId") Long customerId) {
        return personCommandService.reactivate(customerId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{customerId}/deceased")
    @Operation(summary = "Mark customer deceased", description = "Mark as deceased and block dependent operations.")
    public Mono<ResponseEntity<Object>> markCustomerDeceased(@PathVariable("customerId") Long customerId) {
        return personCommandService.markDeceased(customerId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{customerId}/closure-request")
    @Operation(summary = "Request customer closure", description = "Request customer closure once obligations are zero.")
    public Mono<ResponseEntity<Object>> requestCustomerClosure(@PathVariable("customerId") Long customerId) {
        return personCommandService.requestClosure(customerId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{customerId}/confirm-closure")
    @Operation(summary = "Confirm customer closure", description = "Confirm closure after checks pass.")
    public Mono<ResponseEntity<Object>> confirmCustomerClosure(@PathVariable("customerId") Long customerId) {
        return personCommandService.confirmClosure(customerId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{customerId}/merge")
    @Operation(summary = "Merge customer profile", description = "Merge duplicate profile (migrate relations/consents).")
    public Mono<ResponseEntity<Object>> mergeCustomerProfile(
            @PathVariable("customerId") Long customerId,
            @RequestBody Object mergeData) {
        return personCommandService.mergeWith(customerId, mergeData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{customerId}/split")
    @Operation(summary = "Split customer profile", description = "Controlled rollback of a previous merge.")
    public Mono<ResponseEntity<Object>> splitCustomerProfile(
            @PathVariable("customerId") Long customerId,
            @RequestBody Object splitData) {
        return personCommandService.splitFrom(customerId, splitData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{customerId}/lock")
    @Operation(summary = "Lock customer profile", description = "Lock profile for audit/investigation; block writes.")
    public Mono<ResponseEntity<Object>> lockCustomerProfile(@PathVariable("customerId") Long customerId) {
        return personCommandService.lockProfile(customerId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{customerId}/unlock")
    @Operation(summary = "Unlock customer profile", description = "Unlock profile for changes.")
    public Mono<ResponseEntity<Object>> unlockCustomerProfile(@PathVariable("customerId") Long customerId) {
        return personCommandService.unlockProfile(customerId)
                .thenReturn(ResponseEntity.ok().build());
    }
}
