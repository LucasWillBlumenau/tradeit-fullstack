import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { AdvertisementsPage } from "../model/advertisement/advertisement-page";
import { environment } from "../../environments/environment";
import { AdvertisementDetails } from "../model/advertisement/advertisement-details";
import { Advertisement } from "../model/advertisement/advertisement";
import { OfferCreation } from "../model/offer/offer-creation";
import { AdvertisementCreation } from "../model/advertisement/advertisement-creation";
import { OfferDetails } from "../model/offer/offer-details";
import { ContactInfo } from "../model/offer/contact-info";

@Injectable({
    providedIn: "root",
})
export class AdvertisementService {
    private readonly httpClient = inject(HttpClient);
    private readonly apiUrl = environment.apiUrl;

    getAdvertisements(): Observable<AdvertisementsPage> {
        return this.httpClient.get<AdvertisementsPage>(`${this.apiUrl}/api/v1/advertisements`, {
            withCredentials: true,
        });
    }

    getUserAdvertisements(): Observable<Advertisement[]> {
        return this.httpClient.get<Advertisement[]>(`${this.apiUrl}/api/v1/advertisements/mine`, {
            withCredentials: true,
        });
    }

    getAdvertisementById(id: number): Observable<AdvertisementDetails> {
        return this.httpClient.get<AdvertisementDetails>(`${this.apiUrl}/api/v1/advertisements/${id}`, {
            withCredentials: true,
        });
    }

    makeOfferToAdvertisement(advertisementId: number, offerCreation: OfferCreation): Observable<void> {
        const formData = new FormData();
        formData.append("advertisementId", advertisementId.toString());
        formData.append("itemCondition", offerCreation.itemCondition);
        formData.append("additionalMoneyOffer", offerCreation.additionalMoneyOffer);
        formData.append("description", offerCreation.description);
        formData.append("video", offerCreation.video);
        for (const image of offerCreation.images) {
            formData.append("images", image);
        }

        return this.httpClient.post<void>(`${this.apiUrl}/api/v1/offers`, formData, {
            withCredentials: true,
        });
    }

    createAdvertisement(advertisementCreation: AdvertisementCreation): Observable<void> {
        const formData = new FormData();
        formData.append("itemId", advertisementCreation.itemId.toString());
        formData.append("tradingItemId", advertisementCreation.tradingItemId.toString());
        formData.append("extraMoneyAmountRequired", advertisementCreation.additionalMoneyAmountRequired.toString());
        formData.append("description", advertisementCreation.description);
        formData.append("video", advertisementCreation.video);
        formData.append("itemCondition", advertisementCreation.itemCondition);
        for (const image of advertisementCreation.images) {
            formData.append("images", image);
        }

        return this.httpClient.post<void>(`${this.apiUrl}/api/v1/advertisements`, formData, {
            withCredentials: true,
        });
    }

    cancelAdvertisement(advertisementId: number): Observable<void> {
        const url = `${this.apiUrl}/api/v1/advertisements/${advertisementId}/cancel`;
        return this.httpClient.post<void>(url, null, {
            withCredentials: true,
        });
    }

    getOffersFromAdvertisement(advertisementId: number): Observable<OfferDetails[]> {
        const url = `${this.apiUrl}/api/v1/offers`;
        return this.httpClient.get<OfferDetails[]>(url, {
            withCredentials: true,
            params: {
                advertisementId: advertisementId,
            },
        });
    }

    acceptOffer(offerId: number, contactInfo: ContactInfo): Observable<void> {
        const url = `${this.apiUrl}/api/v1/offers/${offerId}/accept`;
        return this.httpClient.post<void>(url, contactInfo, {
            withCredentials: true,
        });
    }
}
