package com.catalis.domain.people.interfaces.dto.commands;

public record RemoveEmailCommand(
    Long partyId,
    Long emailId
) {}