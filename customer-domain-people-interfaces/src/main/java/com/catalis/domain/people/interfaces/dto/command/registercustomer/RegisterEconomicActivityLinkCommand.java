package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RegisterEconomicActivityLinkCommand(
    Long partyId,
    Long economicActivityId,
    BigDecimal annualTurnover,
    BigDecimal annualIncome,
    String incomeSource,
    String employerName,
    String position,
    String notes,
    Long currencyId,
    LocalDateTime startDate,
    LocalDateTime endDate,
    Boolean isPrimary
) {}
