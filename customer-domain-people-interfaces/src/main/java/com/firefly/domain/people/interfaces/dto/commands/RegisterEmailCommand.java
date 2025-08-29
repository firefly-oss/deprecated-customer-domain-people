package com.firefly.domain.people.interfaces.dto.commands;

public record RegisterEmailCommand(
    Long partyId,
    String email,
    String emailKind,
    Boolean isPrimary,
    Boolean isVerified
) {
    
    /**
     * Creates a new RegisterEmailCommand with the specified partyId while preserving all other fields.
     *
     * @param partyId the new partyId to set
     * @return a new RegisterEmailCommand instance with the updated partyId
     */
    public RegisterEmailCommand withPartyId(Long partyId) {
        return new RegisterEmailCommand(
            partyId,
            this.email,
            this.emailKind,
            this.isPrimary,
            this.isVerified
        );
    }
}
