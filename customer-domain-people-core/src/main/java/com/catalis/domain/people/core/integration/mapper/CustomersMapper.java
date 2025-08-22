package com.catalis.domain.people.core.integration.mapper;

import com.catalis.common.customer.sdk.model.PartyDTO;
import com.catalis.common.customer.sdk.model.NaturalPersonDTO;
import com.catalis.common.customer.sdk.model.LegalPersonDTO;
import com.catalis.common.customer.sdk.model.PartyStatusDTO;
import com.catalis.common.customer.sdk.model.PepDTO;
import com.catalis.common.customer.sdk.model.IdentityDocumentDTO;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterNaturalPersonCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterLegalPersonCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyStatusEntryCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPepCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterIdentityDocumentCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.catalis.common.customer.sdk.model.AddressDTO;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterAddressCommand;
import com.catalis.common.customer.sdk.model.EmailDTO;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterEmailCommand;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CustomersMapper {

    @Mapping(target = "partyType", source = "partyType", qualifiedByName = "mapPartyType")
    PartyDTO toPartyDTO(RegisterPartyCommand command);

    @Mapping(target = "gender", source = "gender", qualifiedByName = "mapGender")
    @Mapping(target = "maritalStatus", source = "maritalStatus", qualifiedByName = "mapMaritalStatus")
    @Mapping(target = "residencyStatus", source = "residencyStatus", qualifiedByName = "mapResidencyStatus")
    NaturalPersonDTO toNaturalPersonDTO(RegisterNaturalPersonCommand command);

    LegalPersonDTO toLegalPersonDTO(RegisterLegalPersonCommand command);

    @Mapping(target = "statusCode", source = "statusCode", qualifiedByName = "mapStatusCode")
    PartyStatusDTO toPartyStatusDTO(RegisterPartyStatusEntryCommand command);

    @Mapping(target = "pepNotes", source = "notes")
    PepDTO toPepDTO(RegisterPepCommand command);

    IdentityDocumentDTO toIdentityDocumentDTO(RegisterIdentityDocumentCommand command);

    @Mapping(target = "addressType", source = "addressType", qualifiedByName = "mapAddressType")
    AddressDTO toAddressDTO(RegisterAddressCommand command);

    @Mapping(target = "emailType", source = "emailType", qualifiedByName = "mapEmailType")
    EmailDTO toEmailDTO(RegisterEmailCommand command);

    @Named("mapPartyType")
    default PartyDTO.PartyTypeEnum mapPartyType(String partyType) {
        return Optional.ofNullable(partyType)
                .map(PartyDTO.PartyTypeEnum::fromValue)
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
    default AddressDTO.AddressTypeEnum mapAddressType(String addressType) {
        return Optional.ofNullable(addressType)
                .map(AddressDTO.AddressTypeEnum::fromValue)
                .orElse(null);
    }

    @Named("mapEmailType")
    default EmailDTO.EmailTypeEnum mapEmailType(String emailType) {
        return Optional.ofNullable(emailType)
                .map(EmailDTO.EmailTypeEnum::fromValue)
                .orElse(null);
    }
}
