import { Component, inject, OnInit, output, signal } from "@angular/core";
import { ModalFormWrapper } from "../../home/shared/modal-form-wrapper/modal-form-wrapper";
import { Field } from "../../home/shared/field/field";
import { CategoryService } from "../../../services/category-service";
import { CategoriesPage } from "../../../model/category/categories-page";
import { FormBuilder, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { ItemCreation } from "../../../model/item/item-creation";

@Component({
    selector: "app-create-item-form",
    imports: [ModalFormWrapper, Field, ReactiveFormsModule],
    templateUrl: "./create-item-form.html",
    styleUrl: "./create-item-form.css",
})
export class CreateItemForm implements OnInit {
    public readonly createItem = output<ItemCreation>();

    protected readonly formGroup: FormGroup;
    protected readonly categoriesPage = signal<CategoriesPage | null>(null);

    private readonly categoryService = inject(CategoryService);

    constructor() {
        const formBuilder = inject(FormBuilder);
        this.formGroup = formBuilder.group({
            name: [""],
            categoryId: [""],
        });
    }

    get name() {
        return this.formGroup.get("name");
    }

    get categoryId() {
        return this.formGroup.get("categoryId");
    }

    ngOnInit(): void {
        const firstPage = 0;
        this.categoryService.getCategoriesFromPage(firstPage).subscribe({
            next: (categoriesPage) => {
                this.categoriesPage.set(categoriesPage);
            },
        });
    }

    submitForm() {
        const name = this.name?.value as string;
        const categoryId = this.categoryId?.value as number;

        const itemCreation = { name, categoryId } as ItemCreation;
        this.createItem.emit(itemCreation);

        this.formGroup.reset();
    }
}
