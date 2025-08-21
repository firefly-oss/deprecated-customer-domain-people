package com.catalis.domain.people.interfaces.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PersonView", description = "Output DTO to represent a consolidated customer profile")
public class PersonView {
    @Schema(description = "Unique identifier of the customer")
    private Long id;

    @Schema(description = "Tax identifier (unique)")
    private String taxId;

    @Schema(description = "Full name")
    private String name;

    @Schema(description = "Birth date (ISO-8601)")
    private LocalDate birthDate;

    @Schema(description = "Contact email")
    private String email;
}