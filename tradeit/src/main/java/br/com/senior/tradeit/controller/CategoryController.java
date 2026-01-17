package br.com.senior.tradeit.controller;

import br.com.senior.tradeit.dto.category.CategoryCreationDTO;
import br.com.senior.tradeit.dto.category.CategoryDetailsDTO;
import br.com.senior.tradeit.dto.category.CategoryUpdateDTO;
import br.com.senior.tradeit.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDetailsDTO> createCategory(
            @RequestBody @Valid CategoryCreationDTO categoryCreationDTO,
            UriComponentsBuilder uriBuilder
    ) {
        var categoryDetails = categoryService.createCategory(categoryCreationDTO);
        var location = uriBuilder.path("/api/v1/categories/{id}")
                .buildAndExpand(categoryDetails.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(categoryDetails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDetailsDTO> searchCategory(@PathVariable Long id) {
        var categoryDetails = categoryService.searchCategory(id);
        return ResponseEntity.ok(categoryDetails);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryDetailsDTO>> searchCategories(Pageable pageable) {
        var categories = categoryService.searchCategories(pageable);
        return ResponseEntity.ok(categories);
    }

    @PutMapping
    public ResponseEntity<Void> updateCategory(@RequestBody @Valid CategoryUpdateDTO categoryUpdateDTO) {
        categoryService.updateCategory(categoryUpdateDTO);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent()
                .build();
    }
}
