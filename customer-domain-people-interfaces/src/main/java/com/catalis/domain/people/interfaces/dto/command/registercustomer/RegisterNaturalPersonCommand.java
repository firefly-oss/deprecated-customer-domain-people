package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.time.LocalDate;

public record RegisterNaturalPersonCommand(
    Long partyId,
    String firstName,
    String middleName,
    String firstSurname,
    String secondSurname,
    LocalDate dateOfBirth,
    String placeOfBirth,
    String gender,
    String maritalStatus,
    String taxIdentificationNumber,
    String residencyStatus,
    Long titleId,
    Long nationalityId,
    Long countryOfResidenceId,
    String avatarUrl
) {}
