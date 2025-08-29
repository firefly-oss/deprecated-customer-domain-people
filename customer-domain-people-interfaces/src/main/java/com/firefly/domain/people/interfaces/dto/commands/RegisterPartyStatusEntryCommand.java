package com.firefly.domain.people.interfaces.dto.commands;

import java.time.LocalDateTime;

public record RegisterPartyStatusEntryCommand(
    Long partyId,
    String statusCode,
    String statusReason,
    LocalDateTime validFrom,
    LocalDateTime validTo
) {}
