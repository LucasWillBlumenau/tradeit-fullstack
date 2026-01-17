package br.com.senior.tradeit.service;

import br.com.senior.tradeit.dto.category.CategoryCreationDTO;
import br.com.senior.tradeit.dto.category.CategoryDetailsDTO;
import br.com.senior.tradeit.dto.category.CategoryUpdateDTO;
import br.com.senior.tradeit.entity.category.Category;
import br.com.senior.tradeit.infra.exception.BadRequestException;
import br.com.senior.tradeit.infra.exception.NotFoundException;
import br.com.senior.tradeit.repository.CategoryRepository;
import br.com.senior.tradeit.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    public CategoryService(CategoryRepository categoryRepository, ItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    public CategoryDetailsDTO createCategory(CategoryCreationDTO categoryCreationDTO) {
        String name = categoryCreationDTO.name().trim();
        if (categoryRepository.existsByName(name)) {
            String message = String.format("a categoria '%s' já existe", name);
            throw new BadRequestException(message);
        }

        Category category = Category.builder()
                .name(name)
                .isActive(true)
                .build();
        categoryRepository.save(category);

        return new CategoryDetailsDTO(category);
    }

    public CategoryDetailsDTO searchCategory(Long id) {
        var category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(NotFoundException::new);
        return new CategoryDetailsDTO(category);
    }

    public Page<CategoryDetailsDTO> searchCategories(Pageable pageable) {
        return categoryRepository.findAllByIsActiveTrue(pageable)
                .map(CategoryDetailsDTO::new);
    }

    @Transactional
    public void updateCategory(CategoryUpdateDTO categoryUpdateDTO) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(categoryUpdateDTO.id())
                .orElseThrow(NotFoundException::new);
        category.setName(categoryUpdateDTO.name());
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(NotFoundException::new);
        category.setIsActive(false);
        itemRepository.inactivateItemsByCategoryId(id);
    }
}
