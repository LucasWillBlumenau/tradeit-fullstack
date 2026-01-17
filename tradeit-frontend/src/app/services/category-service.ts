import { inject, Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { Observable } from "rxjs";
import { CategoriesPage } from "../model/category/categories-page";
import { HttpClient } from "@angular/common/http";
import { CategoryCreation } from "../model/category/category-creation";

@Injectable({
    providedIn: "root",
})
export class CategoryService {
    private readonly apiUrl = environment.apiUrl;
    private readonly httpClient = inject(HttpClient);

    getCategoriesFromPage(page: number): Observable<CategoriesPage> {
        const url = `${this.apiUrl}/api/v1/categories`;
        return this.httpClient.get<CategoriesPage>(url, {
            withCredentials: true,
            params: { page },
        });
    }

    deleteCategoryById(id: number): Observable<void> {
        const url = `${this.apiUrl}/api/v1/categories/${id}`;
        return this.httpClient.delete<void>(url, {
            withCredentials: true,
        });
    }

    createCategory(categoryCreation: CategoryCreation): Observable<void> {
        const url = `${this.apiUrl}/api/v1/categories`;
        return this.httpClient.post<void>(url, categoryCreation, {
            withCredentials: true,
        });
    }
}
