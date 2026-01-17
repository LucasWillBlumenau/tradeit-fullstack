import { Component, inject, input, signal } from "@angular/core";
import { OfferDetails } from "../../../../model/offer/offer-details";
import { AdvertisementService } from "../../../../services/advertisement-service";
import { rxResource } from "@angular/core/rxjs-interop";
import { Slide } from "../../shared/slide/slide";
import { Resource, ResourceType } from "../../../../model/resource/resource";
import { environment } from "../../../../../environments/environment";
import { Modal } from "../../../shared/modal/modal";
import { AcceptOfferForm } from "../accept-offer-form/accept-offer-form";
import { ContactInfo } from "../../../../model/offer/contact-info";

@Component({
    selector: "app-advertisement-offers",
    imports: [Slide, Modal, AcceptOfferForm],
    templateUrl: "./advertisement-offers.html",
    styleUrl: "./advertisement-offers.css",
})
export class AdvertisementOffers {
    public readonly advertisementId = input.required<number>();

    private readonly advertisementService = inject(AdvertisementService);
    private readonly apiUrl = environment.apiUrl;
    private selectedOffer: OfferDetails | null = null;

    protected readonly acceptOfferModalIsOpen = signal<boolean>(false);
    protected readonly offersResource = rxResource({
        params: () => this.advertisementId(),
        stream: ({ params: advertisementId }) => this.advertisementService.getOffersFromAdvertisement(advertisementId),
    });
    protected readonly resources = signal<Resource[]>([]);

    showAcceptOfferModal(offer: OfferDetails) {
        this.selectedOffer = offer;
        this.acceptOfferModalIsOpen.set(true);
    }

    acceptOffer(contactInfo: ContactInfo) {
        console.log("carlos");

        if (this.selectedOffer === null) {
            throw new Error("erro inesperado ao aceitar oferta");
        }
        this.advertisementService.acceptOffer(this.selectedOffer.id, contactInfo).subscribe({
            next: () => {
                // TODO: improve message
                console.log("deu certo");
            },
            error: () => {
                // TODO: implove message
                console.log("deu errado");
            },
        });
    }

    showOfferImages(offer: OfferDetails) {
        const images: Resource[] = offer.imageUrls.map((url) => ({
            type: "image" as ResourceType,
            url: `${this.apiUrl}${url}`,
        }));
        const video = { type: "video" as ResourceType, url: `${this.apiUrl}${offer.videoUrl}` };
        this.resources.set([...images, video]);
    }
}
