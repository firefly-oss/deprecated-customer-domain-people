package com.catalis.domain.people.interfaces.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PartyView", description = "Output DTO to represent a party with its identifier and kind")
public class PartyView {
    @Schema(description = "Unique identifier of the party")
    private Long partyId;

    @Schema(description = "Party kind/type")
    private String partyKind;
}