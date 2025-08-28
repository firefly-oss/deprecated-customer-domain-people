package com.catalis.domain.people.interfaces.dto.commands;

public record RegisterPhoneCommand(
    Long partyId,
    String phoneNumber,
    String phoneKind,
    Boolean isPrimary,
    Boolean isVerified,
    String extension
) {
    
    /**
     * Creates a new RegisterPhoneCommand with the specified partyId while preserving all other fields.
     *
     * @param partyId the new partyId to set
     * @return a new RegisterPhoneCommand instance with the updated partyId
     */
    public RegisterPhoneCommand withPartyId(Long partyId) {
        return new RegisterPhoneCommand(
            partyId,
            this.phoneNumber,
            this.phoneKind,
            this.isPrimary,
            this.isVerified,
            this.extension
        );
    }
}
