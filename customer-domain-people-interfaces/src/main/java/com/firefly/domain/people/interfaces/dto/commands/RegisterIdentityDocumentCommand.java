package com.firefly.domain.people.interfaces.dto.commands;

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
