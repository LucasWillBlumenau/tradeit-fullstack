package br.com.senior.tradeit.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreationDTO(
        @NotBlank
        String name
) {
}
