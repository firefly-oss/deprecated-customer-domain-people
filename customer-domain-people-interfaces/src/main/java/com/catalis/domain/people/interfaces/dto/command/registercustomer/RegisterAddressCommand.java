package com.catalis.domain.people.interfaces.dto.command.registercustomer;

public record RegisterAddressCommand(
    Long addressId,
    Long partyId,
    String addressKind,
    String line1,
    String line2,
    String city,
    String region,
    String postalCode,
    Long countryId,
    Boolean isPrimary,
    Double latitude,
    Double longitude
) {}
