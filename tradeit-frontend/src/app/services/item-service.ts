import { inject, Injectable } from "@angular/core";
import { environment } from "../../environments/environment";
import { Observable } from "rxjs";
import { ItemPage } from "../model/item/item-page";
import { HttpClient } from "@angular/common/http";
import { ItemCreation } from "../model/item/item-creation";

@Injectable({
    providedIn: "root",
})
export class ItemService {
    private readonly apiUrl = environment.apiUrl;
    private readonly httpClient = inject(HttpClient);

    getItemsFromPage(pageNumber: number): Observable<ItemPage> {
        const url = `${this.apiUrl}/api/v1/items`;
        return this.httpClient.get<ItemPage>(url, {
            params: {
                page: pageNumber,
            },
            withCredentials: true,
        });
    }

    createItem(itemCreation: ItemCreation): Observable<void> {
        const url = `${this.apiUrl}/api/v1/items`;
        return this.httpClient.post<void>(url, itemCreation, {
            withCredentials: true,
        });
    }

    deleteById(id: number): Observable<void> {
        const url = `${this.apiUrl}/api/v1/items/${id}`;
        return this.httpClient.delete<void>(url, {
            withCredentials: true,
        });
    }
}
