package com.catalis.domain.people.web.controller;

import com.catalis.domain.people.core.service.CommandService;
import com.catalis.domain.people.core.service.QueryService;
import com.catalis.domain.people.core.service.exceptions.DuplicateTaxIdException;
import com.catalis.domain.people.interfaces.dto.commands.RegisterAddressCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterCustomerCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterEmailCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterPhoneCommand;
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

    private final QueryService queryService;
    private final CommandService commandService;

    @PostMapping
    @Operation(summary = "Register a customer", description = "Registers a customer")
    public Mono<ResponseEntity<Object>> registerCustomer(@Valid @RequestBody RegisterCustomerCommand command) {
        return commandService.register(command)
                .thenReturn(ResponseEntity.noContent().build())
                .onErrorResume(DuplicateTaxIdException.class,
                        ex -> Mono.just(ResponseEntity.status(409).body(null)));
    }

    @PatchMapping("/{partyId}/name")
    @Operation(summary = "Update customer name", description = "Updates the name of an existing customer")
    public Mono<ResponseEntity<Object>> updateCustomerName(
            @PathVariable("partyId") Long partyId,
            @RequestBody String newName) {
        return commandService.updateName(partyId, newName)
                .thenReturn(ResponseEntity.ok().build());
    }

    @GetMapping("/{partyId}")
    @Operation(summary = "Get customer by id", description = "Returns a consolidated customer profile")
    public Mono<ResponseEntity<PersonView>> retrieveCustomerInformation(@PathVariable("partyId") Long partyId) {
        return queryService.getCustomerById(partyId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Address endpoints
    @PostMapping("/{partyId}/addresses")
    @Operation(summary = "Add customer address", description = "Add an address with validity period; prevent primary overlaps.")
    public Mono<ResponseEntity<Object>> addCustomerAddress(
            @PathVariable("partyId") Long partyId,
            @RequestBody RegisterAddressCommand addressCommand) {
        return commandService.addAddress(partyId, addressCommand)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PatchMapping("/{partyId}/addresses/{addressId}")
    @Operation(summary = "Update customer address", description = "Modify address fields keeping version history.")
    public Mono<ResponseEntity<Object>> updateCustomerAddress(
            @PathVariable("partyId") Long partyId,
            @PathVariable("addressId") Long addressId,
            @RequestBody RegisterAddressCommand addressData) {
        return commandService.updateAddress(partyId, addressId, addressData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{partyId}/addresses/{addressId}")
    @Operation(summary = "Remove customer address", description = "Remove or end-date an address; preserve audit.")
    public Mono<ResponseEntity<Object>> removeCustomerAddress(
            @PathVariable("partyId") Long partyId,
            @PathVariable("addressId") Long addressId) {
        return commandService.removeAddress(partyId, addressId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Email endpoints
    @PostMapping("/{partyId}/emails")
    @Operation(summary = "Add customer email", description = "Add email; validate format and uniqueness within customer.")
    public Mono<ResponseEntity<Object>> addCustomerEmail(
            @PathVariable("partyId") Long partyId,
            @RequestBody @Valid RegisterEmailCommand emailCommand) {
        return commandService.addEmail(partyId, emailCommand)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{partyId}/emails/{emailId}")
    @Operation(summary = "Remove customer email", description = "Remove email from profile.")
    public Mono<ResponseEntity<Object>> removeCustomerEmail(
            @PathVariable("partyId") Long partyId,
            @PathVariable("emailId") Long emailId) {
        return commandService.removeEmail(partyId, emailId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Phone endpoints
    @PostMapping("/{partyId}/phones")
    @Operation(summary = "Add customer phone", description = "Add phone in E.164 with label (mobile/landline).")
    public Mono<ResponseEntity<Object>> addCustomerPhone(
            @PathVariable("partyId") Long partyId,
            @RequestBody @Valid RegisterPhoneCommand phoneCommand) {
        return commandService.addPhone(partyId, phoneCommand)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{partyId}/phones/{phoneId}")
    @Operation(summary = "Remove customer phone", description = "Remove phone from profile.")
    public Mono<ResponseEntity<Object>> removeCustomerPhone(
            @PathVariable("partyId") Long partyId,
            @PathVariable("phoneId") Long phoneId) {
        return commandService.removePhone(partyId, phoneId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Preferred channel endpoint
    @PostMapping("/{partyId}/preferred-channel")
    @Operation(summary = "Set preferred channel", description = "Set preferred contact channel (email/phone); one per type.")
    public Mono<ResponseEntity<Object>> setPreferredChannel(
            @PathVariable("partyId") Long partyId,
            @RequestBody Object channelData) {
        return commandService.setPreferredChannel(partyId, channelData)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Authorized signers endpoints
    @PostMapping("/{partyId}/authorized-signers")
    @Operation(summary = "Add authorized signatory", description = "Grant a person authority scope to operate (links with PartyRelationship).")
    public Mono<ResponseEntity<Object>> addAuthorizedSignatory(
            @PathVariable("partyId") Long partyId,
            @RequestBody Object signatoryData) {
        return commandService.addAuthorizedSignatory(partyId, signatoryData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{partyId}/authorized-signers")
    @Operation(summary = "Remove authorized signatory", description = "Revoke authority to operate for the given party.")
    public Mono<ResponseEntity<Object>> removeAuthorizedSignatory(
            @PathVariable("partyId") Long partyId) {
        return commandService.removeAuthorizedSignatory(partyId)
                .thenReturn(ResponseEntity.ok().build());
    }

    // Status management endpoints
    @PostMapping("/{partyId}/dormant")
    @Operation(summary = "Mark customer dormant", description = "Flag profile as dormant due to inactivity.")
    public Mono<ResponseEntity<Object>> markCustomerDormant(@PathVariable("partyId") Long partyId) {
        return commandService.markDormant(partyId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/reactivate")
    @Operation(summary = "Reactivate customer", description = "Reactivate a dormant profile.")
    public Mono<ResponseEntity<Object>> reactivateCustomer(@PathVariable("partyId") Long partyId) {
        return commandService.reactivate(partyId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/deceased")
    @Operation(summary = "Mark customer deceased", description = "Mark as deceased and block dependent operations.")
    public Mono<ResponseEntity<Object>> markCustomerDeceased(@PathVariable("partyId") Long partyId) {
        return commandService.markDeceased(partyId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/closure-request")
    @Operation(summary = "Request customer closure", description = "Request customer closure once obligations are zero.")
    public Mono<ResponseEntity<Object>> requestCustomerClosure(@PathVariable("partyId") Long partyId) {
        return commandService.requestClosure(partyId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/confirm-closure")
    @Operation(summary = "Confirm customer closure", description = "Confirm closure after checks pass.")
    public Mono<ResponseEntity<Object>> confirmCustomerClosure(@PathVariable("partyId") Long partyId) {
        return commandService.confirmClosure(partyId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/merge")
    @Operation(summary = "Merge customer profile", description = "Merge duplicate profile (migrate relations/consents).")
    public Mono<ResponseEntity<Object>> mergeCustomerProfile(
            @PathVariable("partyId") Long partyId,
            @RequestBody Object mergeData) {
        return commandService.mergeWith(partyId, mergeData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/split")
    @Operation(summary = "Split customer profile", description = "Controlled rollback of a previous merge.")
    public Mono<ResponseEntity<Object>> splitCustomerProfile(
            @PathVariable("partyId") Long partyId,
            @RequestBody Object splitData) {
        return commandService.splitFrom(partyId, splitData)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/lock")
    @Operation(summary = "Lock customer profile", description = "Lock profile for audit/investigation; block writes.")
    public Mono<ResponseEntity<Object>> lockCustomerProfile(@PathVariable("partyId") Long partyId) {
        return commandService.lockProfile(partyId)
                .thenReturn(ResponseEntity.ok().build());
    }

    @PostMapping("/{partyId}/unlock")
    @Operation(summary = "Unlock customer profile", description = "Unlock profile for changes.")
    public Mono<ResponseEntity<Object>> unlockCustomerProfile(@PathVariable("partyId") Long partyId) {
        return commandService.unlockProfile(partyId)
                .thenReturn(ResponseEntity.ok().build());
    }
}
