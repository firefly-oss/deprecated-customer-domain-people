package com.catalis.domain.people.interfaces.dto.commands;

public record RemoveAddressCommand(
    Long partyId,
    Long addressId
) {}
