import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { OfferDetailsPage } from "../model/offer/offer-details-page";

@Injectable({
    providedIn: "root",
})
export class OfferService {
    private readonly apiUrl = environment.apiUrl;
    private readonly httpClient = inject(HttpClient);

    getUserOffers(): Observable<OfferDetailsPage> {
        return this.httpClient.get<OfferDetailsPage>(`${this.apiUrl}/api/v1/offers/mine`, {
            withCredentials: true,
        });
    }

    cancelOffer(offerId: number): Observable<void> {
        const url = `${this.apiUrl}/api/v1/offers/${offerId}/cancel`;
        return this.httpClient.post<void>(url, null, { withCredentials: true });
    }
}
