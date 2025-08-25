package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.time.LocalDateTime;

public record RegisterIdentityDocumentCommand(
    Long partyId,
    Long identityDocumentCategoryId,
    Long identityDocumentTypeId,
    String documentNumber,
    Long issuingCountryId,
    LocalDateTime issueDate,
    LocalDateTime expiryDate,
    String issuingAuthority,
    Boolean validated,
    String documentUri
) {}
