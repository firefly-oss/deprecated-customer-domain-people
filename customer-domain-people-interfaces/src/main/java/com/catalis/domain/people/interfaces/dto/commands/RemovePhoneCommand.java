package com.catalis.domain.people.interfaces.dto.commands;

public record RemovePhoneCommand(
    Long partyId,
    Long phoneId
) {}