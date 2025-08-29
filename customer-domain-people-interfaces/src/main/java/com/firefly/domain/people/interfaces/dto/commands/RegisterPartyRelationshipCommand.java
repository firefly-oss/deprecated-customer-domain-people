package com.firefly.domain.people.interfaces.dto.commands;

import java.time.LocalDateTime;

public record RegisterPartyRelationshipCommand(
    Long fromPartyId,
    Long toPartyId,
    Long relationshipTypeId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean active,
    String notes
) {}
