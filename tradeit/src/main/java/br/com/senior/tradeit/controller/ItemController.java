package br.com.senior.tradeit.controller;

import br.com.senior.tradeit.dto.item.ItemCreationDTO;
import br.com.senior.tradeit.dto.item.ItemDetailsDTO;
import br.com.senior.tradeit.dto.item.ItemUpdateDTO;
import br.com.senior.tradeit.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDetailsDTO> createItem(
            @RequestBody @Valid ItemCreationDTO itemCreationDTO,
            UriComponentsBuilder uriBuilder
    ) {
        var itemDetails = itemService.createItem(itemCreationDTO);
        var location = uriBuilder.path("/api/v1/items/{id}")
                .buildAndExpand(itemDetails.id())
                .toUri();
        return ResponseEntity.created(location)
                .body(itemDetails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDetailsDTO> searchItem(@PathVariable Long id) {
        var itemsDetails = itemService.searchItem(id);
        return ResponseEntity.ok(itemsDetails);
    }

    @GetMapping
    public ResponseEntity<Page<ItemDetailsDTO>> searchItems(Pageable pageable) {
        var itemsDetails = itemService.searchItems(pageable);
        return ResponseEntity.ok(itemsDetails);
    }

    @PutMapping
    public ResponseEntity<Void> updateItem(@RequestBody @Valid ItemUpdateDTO itemUpdateDTO) {
        itemService.updateItem(itemUpdateDTO);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent()
                .build();
    }
}
