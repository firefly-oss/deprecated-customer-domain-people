package com.catalis.domain.people.interfaces.dto.command.registercustomer;

public record RegisterAddressCommand(
    Long partyId,
    String addressType,
    String addressLine1,
    String addressLine2,
    String city,
    String province,
    String postalCode,
    String country,
    Boolean isPrimary,
    Double latitude,
    Double longitude,
    String normalizationId,
    Boolean isVerified,
    String formattedAddress,
    String administrativeAreaLevel1,
    String administrativeAreaLevel2,
    String countryCode,
    Long administrativeAreaLevel1Id,
    Long administrativeAreaLevel2Id,
    Long countryId
) {}
