package br.com.senior.tradeit.dto.item;

import jakarta.validation.constraints.NotNull;

public record ItemUpdateDTO(
        @NotNull
        Long id,
        String name,
        Long categoryId
) {
}
