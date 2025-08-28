package com.catalis.domain.people.interfaces.dto.commands;

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
) {
    
    /**
     * Creates a new RegisterAddressCommand with the specified partyId while preserving all other fields.
     *
     * @param partyId the new partyId to set
     * @return a new RegisterAddressCommand instance with the updated partyId
     */
    public RegisterAddressCommand withPartyId(Long partyId) {
        return new RegisterAddressCommand(
            this.addressId,
            partyId,
            this.addressKind,
            this.line1,
            this.line2,
            this.city,
            this.region,
            this.postalCode,
            this.countryId,
            this.isPrimary,
            this.latitude,
            this.longitude
        );
    }
    
    /**
     * Creates a new RegisterAddressCommand with the specified addressId while preserving all other fields.
     *
     * @param addressId the new addressId to set
     * @return a new RegisterAddressCommand instance with the updated addressId
     */
    public RegisterAddressCommand withAddressId(Long addressId) {
        return new RegisterAddressCommand(
            addressId,
            this.partyId,
            this.addressKind,
            this.line1,
            this.line2,
            this.city,
            this.region,
            this.postalCode,
            this.countryId,
            this.isPrimary,
            this.latitude,
            this.longitude
        );
    }
}
