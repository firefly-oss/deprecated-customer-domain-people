package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.util.List;

public record RegisterCustomerCommand(
        RegisterPartyCommand party,
        RegisterNaturalPersonCommand naturalPerson,
        RegisterLegalPersonCommand legalPerson,
        List<RegisterPartyStatusEntryCommand> statusHistory,
        RegisterPepCommand pep, //Natural person only
        List<RegisterIdentityDocumentCommand> identityDocuments,
        List<RegisterAddressCommand> addresses,
        List<RegisterEmailCommand> emails,
        List<RegisterPhoneCommand> phones,
        List<RegisterEconomicActivityLinkCommand> economicActivities,
        RegisterDigitalCredentialCommand digitalCredential, //Natural person only
        List<RegisterConsentCommand> consents, //Natural person only
        List<RegisterPartyProviderCommand> providers,
        List<RegisterPartyRelationshipCommand> relationships,
        List<RegisterPartyGroupMembershipCommand> groupMemberships
) {}
