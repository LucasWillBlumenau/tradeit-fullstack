package br.com.senior.tradeit.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryUpdateDTO(
        @NotNull
        Long id,
        @NotBlank
        String name
) {
}
