import { Component, inject, OnInit, signal } from "@angular/core";
import { ItemService } from "../../services/item-service";
import { ItemPage } from "../../model/item/item-page";
import { Item } from "../../model/item/item";
import { Modal } from "../shared/modal/modal";
import { ModalFormWrapper } from "../home/shared/modal-form-wrapper/modal-form-wrapper";
import { ɵInternalFormsSharedModule } from "@angular/forms";
import { Field } from "../home/shared/field/field";

@Component({
    selector: "app-items",
    imports: [Modal, ModalFormWrapper, ɵInternalFormsSharedModule, Field],
    templateUrl: "./items.html",
    styleUrl: "./items.css",
})
export class Items implements OnInit {
    private readonly itemService = inject(ItemService);

    protected readonly itemsPage = signal<ItemPage | null>(null);
    protected readonly editItemModalIsOpen = signal(false);
    protected readonly showExclusionModal = signal(false);
    protected readonly selectedItem = signal<Item | null>(null);

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

    showEditItemModal(item: Item) {
        this.selectedItem.set(item);
        this.editItemModalIsOpen.set(true);
    }

    loadContentFromPage(page: number) {
        this.itemService.getItemsFromPage(0).subscribe({
            next: (itemsPage: ItemPage) => {
                this.itemsPage.set(itemsPage);
            },
        });
    }
}
