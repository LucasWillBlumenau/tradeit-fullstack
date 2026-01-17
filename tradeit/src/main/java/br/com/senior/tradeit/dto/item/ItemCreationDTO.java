package br.com.senior.tradeit.dto.item;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ItemCreationDTO(
        @NotEmpty
        String name,
        @NotNull
        Long categoryId
) {
}
