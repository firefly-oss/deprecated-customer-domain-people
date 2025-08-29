package com.catalis.domain.people.interfaces.dto.commands;

import java.time.LocalDateTime;

public record PreferredChannelCommand(
    Long partyId,
    Long emailId,
    Long phoneId
) {
    public PreferredChannelCommand withPartyId(Long partyId) {
        return new PreferredChannelCommand(
                partyId,
                this.emailId,
                this.phoneId
        );
    }
}
