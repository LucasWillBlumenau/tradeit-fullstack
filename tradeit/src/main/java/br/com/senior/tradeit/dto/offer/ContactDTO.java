package br.com.senior.tradeit.dto.offer;

import br.com.senior.tradeit.entity.offer.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContactDTO(
        @NotNull
        ContactType contactType,
        @NotBlank
        String contactInfo
) {
}
