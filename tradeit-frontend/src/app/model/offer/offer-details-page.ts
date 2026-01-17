import { Pageable } from "../shared/pageable";
import { OfferDetails } from "./offer-details";

export interface OfferDetailsPage {
    content: OfferDetails[];
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
