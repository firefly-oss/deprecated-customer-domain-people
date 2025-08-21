package com.catalis.domain.people.interfaces.dto.command.registercustomer;

public record RegisterConsentProofRefOrCreateCommand(
    Long consentProofId,
    Long documentId
) {}
