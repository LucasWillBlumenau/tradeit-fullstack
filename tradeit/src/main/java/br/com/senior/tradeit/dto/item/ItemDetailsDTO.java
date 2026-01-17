package br.com.senior.tradeit.dto.item;

import br.com.senior.tradeit.entity.item.Item;

public record ItemDetailsDTO(
        Long id,
        String name,
        Long categoryId
) {

    public ItemDetailsDTO(Item item) {
        this(
                item.getId(),
                item.getName(),
                item.getCategory().getId()
        );
    }
}
