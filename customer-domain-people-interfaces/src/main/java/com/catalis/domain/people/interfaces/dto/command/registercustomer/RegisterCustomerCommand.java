package com.catalis.domain.people.interfaces.dto.command.registercustomer;

import java.util.List;

/**
 * Command record for customer registration operations.
 * 
 * This record encapsulates all the necessary information required to register a customer,
 * supporting both natural persons and legal entities. The structure includes party information,
 * personal details, contact information, relationships, and various customer-specific data.
 * 
 * Some fields are specific to natural persons (pep, consents) and should be null when 
 * registering legal entities. The registration process is orchestrated as a saga to ensure
 * data consistency across multiple related entities.
 */
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
        List<RegisterConsentCommand> consents, //Natural person only
        List<RegisterPartyProviderCommand> providers,
        List<RegisterPartyRelationshipCommand> relationships,
        List<RegisterPartyGroupMembershipCommand> groupMemberships
) {}
