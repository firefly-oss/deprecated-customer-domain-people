package com.catalis.domain.people.interfaces.dto.command.registercustomer;

public record RegisterConsentOriginRefOrCreateCommand(
    Long consentOriginId,
    String name,
    String description,
    String consentType,
    Boolean isActive
) {}
