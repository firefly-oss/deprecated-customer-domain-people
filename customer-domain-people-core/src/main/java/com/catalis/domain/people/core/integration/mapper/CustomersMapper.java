package com.catalis.domain.people.core.integration.mapper;


import com.catalis.core.customer.sdk.model.*;
import com.catalis.domain.people.interfaces.dto.commands.RegisterPartyCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterNaturalPersonCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterLegalPersonCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterPartyStatusEntryCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterPepCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterIdentityDocumentCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterConsentCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterPartyProviderCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterPartyRelationshipCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterPartyGroupMembershipCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.catalis.domain.people.interfaces.dto.commands.RegisterAddressCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterEmailCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterPhoneCommand;
import com.catalis.domain.people.interfaces.dto.commands.RegisterEconomicActivityLinkCommand;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CustomersMapper {

    @Mapping(target = "partyKind", source = "partyKind", qualifiedByName = "mapPartyType")
    PartyDTO toPartyDTO(RegisterPartyCommand command);

    @Mapping(target = "gender", source = "gender", qualifiedByName = "mapGender")
    @Mapping(target = "maritalStatus", source = "maritalStatus", qualifiedByName = "mapMaritalStatus")
    @Mapping(target = "residencyStatus", source = "residencyStatus", qualifiedByName = "mapResidencyStatus")
    NaturalPersonDTO toNaturalPersonDTO(RegisterNaturalPersonCommand command);

    LegalEntityDTO toLegalPersonDTO(RegisterLegalPersonCommand command);

    @Mapping(target = "statusCode", source = "statusCode", qualifiedByName = "mapStatusCode")
    PartyStatusDTO toPartyStatusDTO(RegisterPartyStatusEntryCommand command);

    PoliticallyExposedPersonDTO toPepDTO(RegisterPepCommand command);

    IdentityDocumentDTO toIdentityDocumentDTO(RegisterIdentityDocumentCommand command);

    @Mapping(target = "addressKind", source = "addressKind", qualifiedByName = "mapAddressType")
    AddressDTO toAddressDTO(RegisterAddressCommand command);

    @Mapping(target = "emailKind", source = "emailKind", qualifiedByName = "mapEmailType")
    EmailContactDTO toEmailDTO(RegisterEmailCommand command);

    @Mapping(target = "phoneKind", source = "phoneKind", qualifiedByName = "mapPhoneType")
    PhoneContactDTO toPhoneDTO(RegisterPhoneCommand command);

    PartyEconomicActivityDTO toPartyEconomicActivityDTO(RegisterEconomicActivityLinkCommand command);

    ConsentDTO toConsentDTO(RegisterConsentCommand command);

    PartyProviderDTO toPartyProviderDTO(RegisterPartyProviderCommand command);

    PartyRelationshipDTO toPartyRelationshipDTO(RegisterPartyRelationshipCommand command);

    PartyGroupMembershipDTO toPartyGroupMembershipDTO(RegisterPartyGroupMembershipCommand command);

    @Named("mapPartyType")
    default PartyDTO.PartyKindEnum mapPartyType(String partyType) {
        return Optional.ofNullable(partyType)
                .map(PartyDTO.PartyKindEnum::fromValue)
                .orElse(null);
    }

    @Named("mapGender")
    default NaturalPersonDTO.GenderEnum mapGender(String gender) {
        return Optional.ofNullable(gender)
                .map(NaturalPersonDTO.GenderEnum::fromValue)
                .orElse(null);
    }


    @Named("mapMaritalStatus")
    default NaturalPersonDTO.MaritalStatusEnum mapMaritalStatus(String maritalStatus) {
        return Optional.ofNullable(maritalStatus)
                .map(NaturalPersonDTO.MaritalStatusEnum::fromValue)
                .orElse(null);
    }


    @Named("mapResidencyStatus")
    default NaturalPersonDTO.ResidencyStatusEnum mapResidencyStatus(String residencyStatus) {
        return Optional.ofNullable(residencyStatus)
                .map(NaturalPersonDTO.ResidencyStatusEnum::fromValue)
                .orElse(null);
    }

    @Named("mapStatusCode")
    default PartyStatusDTO.StatusCodeEnum mapStatusCode(String statusCode) {
        return Optional.ofNullable(statusCode)
                .map(PartyStatusDTO.StatusCodeEnum::fromValue)
                .orElse(null);
    }

    @Named("mapAddressType")
    default AddressDTO.AddressKindEnum mapAddressType(String addressType) {
        return Optional.ofNullable(addressType)
                .map(AddressDTO.AddressKindEnum::fromValue)
                .orElse(null);
    }

    @Named("mapEmailType")
    default EmailContactDTO.EmailKindEnum mapEmailType(String emailType) {
        return Optional.ofNullable(emailType)
                .map(EmailContactDTO.EmailKindEnum::fromValue)
                .orElse(null);
    }

    @Named("mapPhoneType")
    default PhoneContactDTO.PhoneKindEnum mapPhoneType(String phoneType) {
        return Optional.ofNullable(phoneType)
                .map(PhoneContactDTO.PhoneKindEnum::fromValue)
                .orElse(null);
    }
}
