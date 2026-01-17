package br.com.senior.tradeit.service;


import br.com.senior.tradeit.dto.item.ItemCreationDTO;
import br.com.senior.tradeit.dto.item.ItemDetailsDTO;
import br.com.senior.tradeit.dto.item.ItemUpdateDTO;
import br.com.senior.tradeit.entity.category.Category;
import br.com.senior.tradeit.entity.item.Item;
import br.com.senior.tradeit.infra.exception.BadRequestException;
import br.com.senior.tradeit.infra.exception.NotFoundException;
import br.com.senior.tradeit.repository.CategoryRepository;
import br.com.senior.tradeit.repository.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
    }

    public ItemDetailsDTO createItem(ItemCreationDTO itemCreationDTO) {
        Long categoryId = itemCreationDTO.categoryId();
        var category = findCategoryById(categoryId);
        var name = itemCreationDTO.name();

        Item item = Item.builder()
                .name(name)
                .category(category)
                .isActive(true)
                .build();
        itemRepository.save(item);

        return new ItemDetailsDTO(item);
    }

    public ItemDetailsDTO searchItem(Long id) {
        var item = findItemById(id);
        return new ItemDetailsDTO(item);
    }


    public Page<ItemDetailsDTO> searchItems(Pageable pageable) {
        return itemRepository.findByIsActiveTrue(pageable)
                .map(ItemDetailsDTO::new);
    }

    @Transactional
    public void updateItem(ItemUpdateDTO itemUpdateDTO) {
        Item item = findItemById(itemUpdateDTO.id());
        String updatedName = itemUpdateDTO.name();
        Long updatedCategoryId = itemUpdateDTO.categoryId();
        if (updatedName == null && updatedCategoryId == null) {
            throw new BadRequestException("é necessário informa ao menos o id da nova categoria ou um novo nome");
        }
        if (updatedName != null) {
            item.setName(updatedName);
        }
        if (updatedCategoryId != null) {
            item.setCategory(findCategoryById(updatedCategoryId));
        }
    }

    private Item findItemById(Long id) {
        return itemRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(NotFoundException::new);
    }

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findByIdAndIsActiveTrue(categoryId)
                .orElseThrow(() -> new BadRequestException("a categoria de id " + categoryId + " não existe"));
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(NotFoundException::new);
        item.setIsActive(false);
    }
}
