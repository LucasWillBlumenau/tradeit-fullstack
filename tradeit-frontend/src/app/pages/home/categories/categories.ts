import { Component, inject, OnInit, signal } from "@angular/core";
import { CategoryService } from "../../../services/category-service";
import { CategoriesPage } from "../../../model/category/categories-page";
import { Category } from "../../../model/category/category";
import { Modal } from "../../shared/modal/modal";
import { CreateCategoryForm } from "./create-category-form/create-category-form";
import { CategoryCreation } from "../../../model/category/category-creation";

@Component({
    selector: "app-categories",
    imports: [Modal, CreateCategoryForm],
    templateUrl: "./categories.html",
    styleUrl: "./categories.css",
})
export class Categories implements OnInit {
    protected readonly categoriesPage = signal<CategoriesPage | null>(null);
    protected readonly createCategoryModalIsOpen = signal(false);

    private readonly categoryService = inject(CategoryService);

    ngOnInit(): void {
        const firstPage = 0;
        this.loadCategoriesPage(firstPage);
    }

    deleteCategory(category: Category) {
        this.categoryService.deleteCategoryById(category.id).subscribe({
            next: () => {
                alert("categoria deletada com sucesso");
                this.reloadPage();
            },
            error: () => {
                // TODO: add error handling
            },
        });
    }

    createCategory(categoryCreation: CategoryCreation) {
        this.categoryService.createCategory(categoryCreation).subscribe({
            next: () => {
                // TODO: improve message
                alert("categoria criada com sucesso");
                this.reloadPage();
            },
            error: () => {
                // TODO: add error handling
            },
        });
    }

    showCreateCategoryModal() {
        this.createCategoryModalIsOpen.set(true);
    }

    reloadPage(): void {
        const categoriesPage = this.categoriesPage();
        if (categoriesPage !== null) {
            this.loadCategoriesPage(categoriesPage.pageable.pageNumber);
        }
    }

    loadCategoriesPage(page: number): void {
        this.categoryService.getCategoriesFromPage(page).subscribe({
            next: (categoriesPage) => {
                this.categoriesPage.set(categoriesPage);
            },
            error: () => {
                // TODO: add error handling
            },
        });
    }
}
