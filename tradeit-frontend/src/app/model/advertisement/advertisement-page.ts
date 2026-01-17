import { Pageable } from "../shared/pageable";
import { Advertisement } from "./advertisement";

export interface AdvertisementsPage {
    content: Advertisement[];
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
