import { Component, inject, output } from "@angular/core";
import { ModalFormWrapper } from "../../shared/modal-form-wrapper/modal-form-wrapper";
import { FormBuilder, FormGroup, ReactiveFormsModule } from "@angular/forms";
import { Field } from "../../shared/field/field";
import { CategoryCreation } from "../../../../model/category/category-creation";

@Component({
    selector: "app-create-category-form",
    imports: [ModalFormWrapper, ReactiveFormsModule, Field],
    templateUrl: "./create-category-form.html",
    styleUrl: "./create-category-form.css",
})
export class CreateCategoryForm {
    public readonly createCategory = output<CategoryCreation>();

    protected readonly formGroup: FormGroup;

    constructor() {
        const formBuilder = inject(FormBuilder);
        this.formGroup = formBuilder.group({
            name: [""],
        });
    }

    get name() {
        return this.formGroup.get("name");
    }

    submitForm() {
        const name = this.name?.value as string;
        const categoryCreation = { name } as CategoryCreation;

        this.createCategory.emit(categoryCreation);

        this.formGroup.reset();
    }
}
