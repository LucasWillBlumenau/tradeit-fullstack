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
class ItemServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemService itemService;


    @Test
    void createItem_shouldCreateItem_whenCategoryIsFound() {
        Category category = demoCategory();
        ItemCreationDTO itemCreationDTO = new ItemCreationDTO("item", 1L);
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(category));

        ItemDetailsDTO itemDetailsDTO = itemService.createItem(itemCreationDTO);
        assertEquals("item", itemDetailsDTO.name());
        assertEquals(1L, itemDetailsDTO.categoryId());
        verify(itemRepository).save(any());
    }

    @Test
    void createItem_shouldThrowNotFoundException_whenCategoryIsNotFound() {
        ItemCreationDTO itemCreationDTO = new ItemCreationDTO("item", 1L);
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        var exc = assertThrows(BadRequestException.class, () -> itemService.createItem(itemCreationDTO));
        assertEquals("a categoria de id 1 não existe", exc.getMessage());
    }

    @Test
    void searchItem_shouldReturnItemDetails_whenItemIsFound() {
        Item item = new Item(1L, "item", demoCategory(), true);
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(item));

        ItemDetailsDTO itemDetails = itemService.searchItem(1L);
        assertEquals(1L, itemDetails.id());
        assertEquals("item", itemDetails.name());
        assertEquals(1L, itemDetails.categoryId());
    }

    @Test
    void searchItem_shouldThrowNotFoundException_whenItemIsNotFound() {
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.searchItem(1L));
    }

    @Test
    void searchItems_shouldReturnItems_whenServiceIsCalled() {
        Pageable pageable = Pageable.ofSize(20);
        List<Item> items = List.of(
                new Item(1L, "item 1", demoCategory(), true),
                new Item(2L, "item 2", demoCategory(), true)
        );
        Page<Item> page = new PageImpl<>(items, pageable, 2);
        when(itemRepository.findByIsActiveTrue(pageable)).thenReturn(page);

        List<ItemDetailsDTO> itemsDetails = itemService.searchItems(pageable)
                .stream()
                .toList();

        assertEquals(2, itemsDetails.size());
        assertEquals(1L, itemsDetails.get(0).id());
        assertEquals("item 1", itemsDetails.get(0).name());
        assertEquals(1L, itemsDetails.get(0).categoryId());
        assertEquals(2L, itemsDetails.get(1).id());
        assertEquals("item 2", itemsDetails.get(1).name());
        assertEquals(1L, itemsDetails.get(1).categoryId());
    }

    @Test
    void updateItem_shouldUpdateItem_whenItemIsFoundAndItemUpdateIsValid() {
        Category otherCategory = new Category(2L, "other category", true);
        Item item = new Item(1L, "item 1", demoCategory(), true);
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO(1L, "new item 1 name", 2L);
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(item));
        when(categoryRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.of(otherCategory));

        itemService.updateItem(itemUpdateDTO);

        assertEquals("new item 1 name", item.getName());
        assertEquals(otherCategory, item.getCategory());
    }

    @Test
    void updateItem_shouldThrowNotFoundException_whenItemIsNotFound() {
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO(1L,  "item 1", 1L);
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemUpdateDTO));
    }

    @Test
    void updateItem_shouldThrowBadRequestException_whenItemUpdateIsNotValid() {
        Item item = new Item(1L, "item 1", demoCategory(), true);
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO(1L,  null, null);
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(item));

        var exc = assertThrows(BadRequestException.class, () -> itemService.updateItem(itemUpdateDTO));
        assertEquals("é necessário informa ao menos o id da nova categoria ou um novo nome", exc.getMessage());
    }

    @Test
    void updateItem_shouldThrowBadRequestException_whenCategoryIsNotFound() {
        Item item = new Item(1L, "item 1", demoCategory(), true);
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO(1L,  null, 1L);
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(item));
        when(categoryRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        var exc = assertThrows(BadRequestException.class, () -> itemService.updateItem(itemUpdateDTO));
        assertEquals("a categoria de id 1 não existe", exc.getMessage());
    }

    @Test
    void deleteItem_shouldDeleteItem_whenItemIsFound() {
        Item item = new Item(1L, "item 1", demoCategory(), true);
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(item));

        itemService.deleteItem(1L);

        assertFalse(item.getIsActive());
    }

    @Test
    void deleteItem_shouldThrowNotFoundException_whenItemIsNotFound() {
        when(itemRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.deleteItem(1L));
    }

    private Category demoCategory() {
        return new Category(1L, "category", true);
    }

}