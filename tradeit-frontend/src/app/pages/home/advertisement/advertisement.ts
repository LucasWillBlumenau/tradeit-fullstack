import { Component, HostListener, inject, OnInit, signal } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { AdvertisementService } from "../../../services/advertisement-service";
import { AdvertisementDetails } from "../../../model/advertisement/advertisement-details";
import { Slide } from "../shared/slide/slide";
import { Resource, ResourceType } from "../../../model/resource/resource";
import { environment } from "../../../../environments/environment";
import { TranslateItemConditionPipe } from "../../../core/pipes/translate-item-condition-pipe";
import { CurrencyPipe } from "@angular/common";
import { Modal } from "../../shared/modal/modal";
import { CreateOfferForm } from "./create-offer-form/create-offer-form";
import { OfferCreation } from "../../../model/offer/offer-creation";

@Component({
    selector: "app-advertisement",
    imports: [Slide, TranslateItemConditionPipe, CurrencyPipe, Modal, CreateOfferForm],
    templateUrl: "./advertisement.html",
    styleUrl: "./advertisement.css",
})
export class Advertisement implements OnInit {
    private readonly activatedRoute = inject(ActivatedRoute);
    private readonly advertisementService = inject(AdvertisementService);
    private readonly apiUrl = environment.apiUrl;

    protected readonly createOfferModalIsOpen = signal<boolean>(false);
    protected readonly advertisement = signal<AdvertisementDetails | null>(null);
    protected resources = signal<Resource[]>([]);

    ngOnInit(): void {
        const id = Number.parseInt(this.activatedRoute.snapshot.paramMap.get("id") as string);
        this.advertisementService.getAdvertisementById(id).subscribe({
            next: (advertisement) => this.setAdvertisement(advertisement),
        });
    }

    setAdvertisement(advertisement: AdvertisementDetails): void {
        this.advertisement.set(advertisement);
        const imagesResources = advertisement.imageUrls.map((imageUrl) => ({
            type: "image" as ResourceType,
            url: `${this.apiUrl}${imageUrl}`,
        }));
        const videoResource = { type: "video" as ResourceType, url: `${this.apiUrl}${advertisement.videoUrl}` };
        this.resources.set([...imagesResources, videoResource]);
    }

    createOffer(offerCreation: OfferCreation): void {
        const advertisementId = this.advertisement()?.id as number;
        this.advertisementService.makeOfferToAdvertisement(advertisementId, offerCreation).subscribe({
            next: () => {
                alert("oferta criada com sucesso"); // TODO: improve response
                this.createOfferModalIsOpen.set(false);
            },
            error: (err) => {
                // TODO: add proper error handling
                console.log("não é possível fazer uma oferta para seus própios anúncios");
            },
        });
    }

    openCreateOfferForm(): void {
        this.createOfferModalIsOpen.set(true);
    }

    @HostListener("window:keyup", ["$event"])
    closeCreateOfferForm(event: KeyboardEvent): void {
        if (event.key === "Escape") {
            this.createOfferModalIsOpen.set(false);
        }
    }
}
