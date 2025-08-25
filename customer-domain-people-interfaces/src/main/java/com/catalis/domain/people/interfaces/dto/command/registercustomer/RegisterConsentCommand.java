package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.time.LocalDateTime;

public record RegisterConsentCommand(
    Long partyId,
    Long consentTypeId,
    Boolean granted,
    LocalDateTime grantedAt,
    LocalDateTime revokedAt,
    String channel
) {}
