package com.catalis.domain.people.core.integration.mapper;

import com.catalis.common.customer.sdk.model.PartyDTO;
import com.catalis.common.customer.sdk.model.NaturalPersonDTO;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterPartyCommand;
import com.catalis.domain.people.interfaces.dto.command.registercustomer.RegisterNaturalPersonCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface CustomersMapper {

    @Mapping(target = "partyType", source = "partyType", qualifiedByName = "mapPartyType")
    PartyDTO toPartyDTO(RegisterPartyCommand command);

    @Mapping(target = "gender", source = "gender", qualifiedByName = "mapGender")
    @Mapping(target = "maritalStatus", source = "maritalStatus", qualifiedByName = "mapMaritalStatus")
    @Mapping(target = "residencyStatus", source = "residencyStatus", qualifiedByName = "mapResidencyStatus")
    NaturalPersonDTO toNaturalPersonDTO(RegisterNaturalPersonCommand command);

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
}
