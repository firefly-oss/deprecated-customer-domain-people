package com.catalis.domain.people.interfaces.dto.commands;

public record RegisterPartyCommand(
        String partyKind,
        String preferredLanguage,
        String recordSource
) {}
