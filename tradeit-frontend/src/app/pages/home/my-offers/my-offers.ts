import { Component, inject, OnInit, signal } from "@angular/core";
import { OfferService } from "../../../services/offer-service";
import { environment } from "../../../../environments/environment";
import { OfferDetailsPage } from "../../../model/offer/offer-details-page";
import { TranslateOfferStatusPipe } from "../../../core/pipes/translate-offer-status-pipe";
import { RouterLink } from "@angular/router";
import { OfferDetails } from "../../../model/offer/offer-details";

@Component({
    selector: "app-my-offers",
    imports: [TranslateOfferStatusPipe, RouterLink],
    templateUrl: "./my-offers.html",
    styleUrl: "./my-offers.css",
})
export class MyOffers implements OnInit {
    private readonly offerService = inject(OfferService);

    protected readonly apiUrl = environment.apiUrl;
    protected readonly offers = signal<OfferDetailsPage | null>(null);

    ngOnInit(): void {
        this.loadOffers();
    }

    cancelOffer(offerId: number): void {
        // TODO: ask user if it's sure of cancelling the offer
        this.offerService.cancelOffer(offerId).subscribe({
            next: () => {
                this.loadOffers();
            },
        });
    }

    loadOffers(): void {
        this.offerService.getUserOffers().subscribe({
            next: (offerPage) => {
                this.offers.set(offerPage);
            },
        });
    }

    showContactInfo(offerDetails: OfferDetails) {
        // TODO: show modal with offers information
        console.log(offerDetails.contact);
    }
}
