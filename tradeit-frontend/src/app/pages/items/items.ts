import { Component, inject, OnInit, signal } from "@angular/core";
import { ItemService } from "../../services/item-service";
import { ItemPage } from "../../model/item/item-page";
import { Item } from "../../model/item/item";
import { Modal } from "../shared/modal/modal";
import { CreateItemForm } from "./create-item-form/create-item-form";
import { ItemCreation } from "../../model/item/item-creation";
@Component({
    selector: "app-items",
    imports: [Modal, CreateItemForm],
    templateUrl: "./items.html",
    styleUrl: "./items.css",
})
export class Items implements OnInit {
    private readonly itemService = inject(ItemService);

    protected readonly itemsPage = signal<ItemPage | null>(null);
    protected readonly createItemModalIsOpen = signal(false);

    ngOnInit(): void {
        const firstPage = 0;
        this.loadContentFromPage(firstPage);
    }

    removeItem(item: Item) {
        this.itemService.deleteById(item.id).subscribe({
            next: () => {
                console.log("item deletado com sucesso");
            },
        });
    }

    loadContentFromPage(page: number) {
        this.itemService.getItemsFromPage(page).subscribe({
            next: (itemsPage: ItemPage) => {
                this.itemsPage.set(itemsPage);
            },
        });
    }

    openCreateItemModal() {
        this.createItemModalIsOpen.set(true);
    }

    createItem(itemCreation: ItemCreation) {
        this.itemService.createItem(itemCreation).subscribe({
            next: () => {
                // TODO: improve message
                alert("item criado com sucesso!");
                this.createItemModalIsOpen.set(false);
            },
            error: () => {
                // TODO: add error handling
            },
        });
    }
}
