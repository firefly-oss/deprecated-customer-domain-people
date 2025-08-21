package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.time.LocalDateTime;

public record RegisterPepCommand(
    Long partyId,
    Boolean isPep,
    String pepCategory,
    String publicPosition,
    String countryOfPosition,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String notes
) {}
