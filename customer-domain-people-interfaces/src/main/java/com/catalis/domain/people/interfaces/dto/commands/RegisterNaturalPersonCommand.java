package com.catalis.domain.people.interfaces.dto.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RegisterNaturalPersonCommand (
    Long naturalPersonId,
    Long partyId,
    String title,
    String givenName,
    String middleName,
    String familyName1,
    String familyName2,
    LocalDate dateOfBirth,
    String birthPlace,
    Long birthCountryId,
    Long nationalityCountryId,
    String gender,
    String maritalStatus,
    String taxIdNumber,
    String residencyStatus,
    String occupation,
    BigDecimal monthlyIncome,
    String suffix,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
){}
