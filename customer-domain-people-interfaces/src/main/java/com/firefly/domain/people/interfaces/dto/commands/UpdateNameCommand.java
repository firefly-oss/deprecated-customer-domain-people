package com.firefly.domain.people.interfaces.dto.commands;

public record UpdateNameCommand(
    Long partyId,
    String newName
) {}