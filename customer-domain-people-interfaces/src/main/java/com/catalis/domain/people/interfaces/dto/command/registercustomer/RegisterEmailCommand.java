package com.catalis.domain.people.interfaces.dto.command.registercustomer;

public record RegisterEmailCommand(
    Long partyId,
    String email,
    String emailKind,
    Boolean isPrimary,
    Boolean isVerified
) {}
