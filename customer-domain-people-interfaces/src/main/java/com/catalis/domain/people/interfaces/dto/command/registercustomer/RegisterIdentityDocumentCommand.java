package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.time.LocalDateTime;

public record RegisterIdentityDocumentCommand(
    Long partyId,
    Long identityDocumentTypeId,
    String documentNumber,
    String countryOfIssue,
    LocalDateTime issueDate,
    LocalDateTime expiryDate,
    String issuingAuthority,
    Boolean isValidated,
    Long documentId
) {}
