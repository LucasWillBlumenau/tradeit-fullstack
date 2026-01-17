package br.com.senior.tradeit.service;

import br.com.senior.tradeit.dto.category.CategoryCreationDTO;
import br.com.senior.tradeit.dto.category.CategoryDetailsDTO;
import br.com.senior.tradeit.dto.category.CategoryUpdateDTO;
import br.com.senior.tradeit.entity.category.Category;
import br.com.senior.tradeit.infra.exception.BadRequestException;
import br.com.senior.tradeit.infra.exception.NotFoundException;
import br.com.senior.tradeit.repository.CategoryRepository;
import br.com.senior.tradeit.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_shouldCreateCategory_whenCategoryCreationIsValid() {
        CategoryCreationDTO categoryCreation = new CategoryCreationDTO("category");
        when(categoryRepository.existsByName(categoryCreation.name())).thenReturn(false);

        CategoryDetailsDTO categoryDetails = categoryService.createCategory(categoryCreation);
        assertEquals("category", categoryDetails.name());
        verify(categoryRepository).save(any());
    }

    @Test
    void createCategory_shouldThrowBadRequestError_whenCategoryNameIsAlreadyTaken() {
        CategoryCreationDTO categoryCreation = new CategoryCreationDTO("category");
        when(categoryRepository.existsByName(categoryCreation.name())).thenReturn(true);

        var exc = assertThrows(BadRequestException.class, () -> categoryService.createCategory(categoryCreation));
        assertEquals("a categoria 'category' já existe", exc.getMessage());
    }

    @Test
    void searchCategory_shouldReturnCategoryDetails_whenCategoryIsFound() {
        Category category = new Category(1L, "category", true);
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(category));

        CategoryDetailsDTO categoryDetails = categoryService.searchCategory(1L);
        assertEquals(1L, categoryDetails.id());
        assertEquals("category", categoryDetails.name());
    }

    @Test
    void searchCategory_shouldThrowNotFoundException_whenCategoryIsNotFound() {
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.searchCategory(1L));
    }

    @Test
    void searchCategories_shouldReturnPageOfCategories_whenServiceIsCalled() {
        List<Category> categories = List.of(
                new Category(1L, "category 1", true),
                new Category(2L, "category 2", true),
                new Category(3L, "category 3", true)
        );
        Pageable pageable = Pageable.ofSize(20);
        Page<Category> page = new PageImpl(categories, pageable, 3);
        when(categoryRepository.findAllByIsActiveTrue(pageable)).thenReturn(page);

        List<CategoryDetailsDTO> categoriesDetails = categoryService.searchCategories(pageable)
                .stream()
                .toList();
        assertEquals(3, categoriesDetails.size());
    }

    @Test
    void updateCategory_shouldUpdateCategory_whenCategoryIsFound() {
        Category category = new Category(1L, "category", true);
        CategoryUpdateDTO categoryUpdate = new CategoryUpdateDTO(1L, "new category name");
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(category));

        categoryService.updateCategory(categoryUpdate);
        assertEquals("new category name", category.getName());
    }

    @Test
    void updateCategory_shouldThrowNotFoundException_whenCategoryIsNotFound() {
        CategoryUpdateDTO categoryUpdate = new CategoryUpdateDTO(1L, "new category name");
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(categoryUpdate));
    }

    @Test
    void deleteCategory_shouldDeleteCategory_whenCategoryIsFound() {
        Category category = new Category(1L, "category", true);
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        assertFalse(category.getIsActive());
        verify(itemRepository).inactivateItemsByCategoryId(1L);
    }

    @Test
    void deleteCategory_shouldThrowNotFoundException_whenCategoryIsNotFound() {
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(1L));
    }

}