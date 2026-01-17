package br.com.senior.tradeit.dto.category;

import br.com.senior.tradeit.entity.category.Category;

public record CategoryDetailsDTO(
        Long id,
        String name
) {

    public CategoryDetailsDTO(Category category) {
        this(category.getId(), category.getName());
    }
}
