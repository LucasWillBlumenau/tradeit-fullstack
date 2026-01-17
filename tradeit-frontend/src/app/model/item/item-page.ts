import { Pageable } from "../shared/pageable";
import { Item } from "./item";

export interface ItemPage {
    content: Item[];
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
