package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.time.LocalDateTime;

public record RegisterPartyRelationshipCommand(
    Long fromPartyId,
    Long toPartyId,
    Long relationshipTypeId,
    String relationshipTypeDescription,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isActive,
    String notes
) {}
