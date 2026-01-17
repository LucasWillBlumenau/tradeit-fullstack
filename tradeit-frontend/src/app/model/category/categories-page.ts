import { Pageable } from "../shared/pageable";
import { Category } from "./category";

export interface CategoriesPage {
    content: Category[];
    empty: boolean;
    first: boolean;
    last: true;
    number: number;
    numberOfElements: number;
    pageable: Pageable;
    size: number;
    sort: {
        empty: true;
        sorted: false;
        unsorted: true;
    };
    totalElements: number;
    totalPages: number;
}
