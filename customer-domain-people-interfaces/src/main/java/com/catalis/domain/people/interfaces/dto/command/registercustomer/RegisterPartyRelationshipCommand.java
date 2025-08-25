package com.catalis.domain.people.interfaces.dto.command.registercustomer;

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
