package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.time.LocalDateTime;

public record RegisterDigitalCredentialCommand(
    Long partyId,
    String ciamUserId,
    String credentialType,
    Boolean isActive,
    LocalDateTime lastLogin
) {}
