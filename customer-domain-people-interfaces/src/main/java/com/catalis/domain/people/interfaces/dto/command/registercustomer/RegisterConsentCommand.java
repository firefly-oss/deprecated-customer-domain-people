package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.time.LocalDateTime;

public record RegisterConsentCommand(
    Long partyId,
    Long consentTypeId,
    Boolean isGranted,
    LocalDateTime dateGranted,
    LocalDateTime dateRevoked,
    String grantedChannel,
    String revokedChannel,
    String revocationReason,
    String consentVersion,
    LocalDateTime expiryDate,
    String ipAddress,
    String userAgent,
    RegisterConsentOriginRefOrCreateCommand origin,
    RegisterConsentProofRefOrCreateCommand proof
) {}
