package com.catalis.domain.people.interfaces.dto.command.registercustomer;

public record RegisterPhoneCommand(
    Long partyId,
    String phoneNumber,
    String phoneKind,
    Boolean isPrimary,
    Boolean isVerified,
    String extension
) {}
