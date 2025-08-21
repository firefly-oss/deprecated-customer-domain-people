package com.catalis.domain.people.interfaces.dto.command.registercustomer;

public record RegisterPartyCommand(
        String partyType,
        String preferredLanguage,
        String recordSource
) {}
