package com.firefly.domain.people.interfaces.dto.commands;

import java.time.LocalDateTime;

public record RegisterPepCommand(
    Long partyId,
    Boolean pep,
    String category,
    String publicPosition,
    Long countryOfPositionId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String notes
) {}
