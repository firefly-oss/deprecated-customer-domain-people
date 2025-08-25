package com.catalis.domain.people.interfaces.dto.command.registercustomer;

public record RegisterPartyCommand(
        String partyKind,
        String preferredLanguage,
        String recordSource
) {}
