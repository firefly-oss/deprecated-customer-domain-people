package com.catalis.domain.people.interfaces.dto.command.registercustomer;

public record RegisterEmailCommand(
    Long partyId,
    String emailAddress,
    String emailType,
    Boolean isPrimary,
    Boolean isVerified
) {}
