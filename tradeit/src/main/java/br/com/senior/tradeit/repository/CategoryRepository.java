package br.com.senior.tradeit.repository;

import br.com.senior.tradeit.entity.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface CategoryRepository extends Repository<Category, Long> {
    void save(Category category);
    Optional<Category> findByIdAndIsActiveTrue(Long id);
    boolean existsByName(String name);
    Page<Category> findAllByIsActiveTrue(Pageable pageable);
    void deleteById(Long id);
}
