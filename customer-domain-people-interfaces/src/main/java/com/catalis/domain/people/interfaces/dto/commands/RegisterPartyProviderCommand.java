package com.catalis.domain.people.interfaces.dto.commands;

import java.time.LocalDateTime;

public record RegisterPartyProviderCommand(
    Long partyId,
    String providerName,
    String externalReference,
    String providerStatus,
    LocalDateTime lastSyncDate
) {}
