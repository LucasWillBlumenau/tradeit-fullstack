package br.com.senior.tradeit.repository;

import br.com.senior.tradeit.entity.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface ItemRepository extends Repository<Item, Long> {
    void save(Item item);
    Optional<Item> findByIdAndIsActiveTrue(Long id);
    Page<Item> findByIsActiveTrue(Pageable pageable);
    @Modifying
    @Query("""
            UPDATE
                Item
            SET
                isActive = false
            WHERE
                category.id = :categoryId
            """)
    void inactivateItemsByCategoryId(Long categoryId);
}
