package com.firefly.domain.people.interfaces.dto.commands;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RegisterEconomicActivityLinkCommand(
    Long partyId,
    Long economicActivityId,
    BigDecimal annualTurnover,
    String currencyCode,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isPrimary
) {}
