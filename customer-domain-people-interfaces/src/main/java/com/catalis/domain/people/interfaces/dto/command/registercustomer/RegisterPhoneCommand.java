package com.catalis.domain.people.interfaces.dto.command.registercustomer;

public record RegisterPhoneCommand(
    Long partyId,
    String phoneNumber,
    String phoneType,
    Boolean isPrimary,
    Boolean isVerified,
    String extension
) {}
