package com.catalis.domain.people.interfaces.dto.commands;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RegisterLegalPersonCommand(
        Long legalEntityId,
        Long partyId,
        String legalName,
        String tradeName,
        String registrationNumber,
        String taxIdNumber,
        Long legalFormId,
        LocalDate incorporationDate,
        String industryDescription,
        Long headcount,
        BigDecimal shareCapital,
        String websiteUrl,
        Long incorporationCountryId
) {}
