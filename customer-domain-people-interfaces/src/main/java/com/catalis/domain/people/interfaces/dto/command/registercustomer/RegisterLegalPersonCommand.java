package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterLegalPersonCommand(
    Long partyId,
    String legalName,
    String tradeName,
    String registrationNumber,
    String taxIdentificationNumber,
    Long legalFormId,
    LocalDate dateOfIncorporation,
    String businessActivity,
    Long numberOfEmployees,
    BigDecimal shareCapital,
    String websiteUrl,
    String incorporationCountry,
    String phoneNumber,
    String emailAddress,
    String mainContactName,
    String mainContactTitle,
    String logoUrl
) {}
