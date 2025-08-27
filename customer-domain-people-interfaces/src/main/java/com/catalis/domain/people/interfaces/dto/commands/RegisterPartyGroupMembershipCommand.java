package com.catalis.domain.people.interfaces.dto.commands;

import java.time.LocalDateTime;

public record RegisterPartyGroupMembershipCommand(
    Long partyId,
    Long groupId,
    Boolean isActive,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String notes
) {}
